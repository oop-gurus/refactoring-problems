package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class PostOfficeBuilder(
    private val mailSpamService: MailSpamService,
    private val mailTemplateRepository: MailTemplateRepository,
    private val handlebars: Handlebars,
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate,
    private val javaMailSender: JavaMailSender,
    private val mailRepository: MailRepository,
    private val scheduledExecutorService: ScheduledExecutorService,
) {
    private var toAddressSupplier: () -> String = { throw IllegalStateException("toAddressSupplier is not set") }
    private var fromAddressSupplier: () -> String = { throw IllegalStateException("fromAddressSupplier is not set") }
    private var htmlTemplateNameSupplier: () -> String = { throw IllegalStateException("htmlTemplateNameSupplier is not set") }
    private var htmlTemplateSupplier: () -> Template = { throw IllegalStateException("htmlTemplateSupplier is not set") }
    private var titleSupplier: () -> String = { throw IllegalStateException("titleSupplier is not set") }
    private var fromNameSupplier: () -> String = { throw IllegalStateException("fromNameSupplier is not set") }
    private var htmlTemplateParametersSupplier: () -> HtmlTemplateParameters = { throw IllegalStateException("htmlTemplateParametersSupplier is not set") }
    private var fileAttachmentDtoListSupplier: () -> List<FileAttachmentDto> = { throw IllegalStateException("fileAttachmentDtoListSupplier is not set") }
    private var sendAfterSupplier: () -> SendAfter? = { throw IllegalStateException("sendAfterSupplier is not set") }


    fun toAddress(toAddress: String): PostOfficeBuilder {
        toAddressSupplier = when {
            mailSpamService.needBlockByDomainName(toAddress) -> {
                { throw RuntimeException("도메인 차단") }
            }

            mailSpamService.needBlockByRecentSuccess(toAddress) -> {
                { throw RuntimeException("최근 메일 발송 실패로 인한 차단") }
            }

            Regex(".+@.*\\..+").matches(toAddress).not() -> {
                { throw RuntimeException("이메일 형식 오류") }
            }

            else -> {
                { toAddress }
            }
        }
        return this
    }

    fun fromAddress(fromAddress: String): PostOfficeBuilder {
        fromAddressSupplier = when {
            Regex(".+@.*\\..+").matches(fromAddress).not() -> {
                { throw RuntimeException("이메일 형식 오류") }
            }

            else -> {
                { fromAddress }
            }
        }
        return this
    }

    fun htmlTemplateName(htmlTemplateName: String): PostOfficeBuilder {
        htmlTemplateNameSupplier = when {
            htmlTemplateName.isBlank() -> {
                { throw RuntimeException("템플릿 이름이 비어있습니다") }
            }

            else -> {
                { htmlTemplateName }
            }
        }

        val htmlTemplate = mailTemplateRepository.findByName(htmlTemplateName)
        htmlTemplateSupplier = when {
            htmlTemplate == null -> {
                { throw RuntimeException("템플릿이 존재하지 않습니다: [$htmlTemplateName]") }
            }

            else -> {
                val template = handlebars.compileInline(htmlTemplate.htmlBody)
                ({ template })
            }
        }
        return this
    }


    fun title(title: String): PostOfficeBuilder {
        titleSupplier = when {
            title.isBlank() -> {
                { throw RuntimeException("제목이 비어있습니다") }
            }

            else -> {
                { title }
            }
        }
        return this
    }


    fun fromName(fromName: String): PostOfficeBuilder {
        fromNameSupplier = when {
            fromName.isBlank() -> {
                { throw RuntimeException("이름이 비어있습니다") }
            }

            else -> {
                { fromName }
            }
        }
        return this
    }

    fun htmlTemplateParameters(htmlTemplateParameters: Map<String, Any>): PostOfficeBuilder {
        htmlTemplateParametersSupplier = {
            HtmlTemplateParameters(
                parameters = htmlTemplateParameters,
                objectMapper = objectMapper,
            )
        }
        return this
    }

    fun fileAttachments(fileAttachments: List<FileAttachment>): PostOfficeBuilder {
        val fileAttachmentDtoList = fileAttachments.mapIndexed { index, attachment ->
            val fileAttachmentDto = restTemplate.execute(
                attachment.url,
                HttpMethod.GET,
                null,
                { clientHttpResponse: ClientHttpResponse ->
                    val id = "file-${index}-${java.util.UUID.randomUUID()}"
                    val tempFile = File.createTempFile(id, "")
                    clientHttpResponse.body.copyTo(FileOutputStream(tempFile))
                    FileAttachmentDto(
                        resultFile = tempFile,
                        name = attachment.name,
                        clientHttpResponse = clientHttpResponse
                    )
                }) ?: throw RuntimeException("파일 초기화 실패")

            if (fileAttachmentDto.resultFile.length() != fileAttachmentDto.clientHttpResponse.headers.contentLength) {
                throw RuntimeException("파일 크기 불일치")
            }
            if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(fileAttachmentDto.clientHttpResponse.headers.contentLength)) {
                throw RuntimeException("파일 크기 초과")
            }
            fileAttachmentDto
        }

        fileAttachmentDtoListSupplier = { fileAttachmentDtoList }
        return this
    }

    fun sendAfterSeconds(sendAfterSeconds: Long?): PostOfficeBuilder {
        if (sendAfterSeconds == null) {
            sendAfterSupplier = { null }
            return this
        }
        if (sendAfterSeconds <= 0) {
            throw RuntimeException("sendAfterSeconds는 0 이상이어야 합니다")
        }

        sendAfterSupplier = {
            SendAfter(
                amount = sendAfterSeconds,
                unit = TimeUnit.SECONDS
            )
        }
        return this
    }

    fun build(): PostOffice {
        return PostOffice(
            javaMailSender = javaMailSender,

            mailRepository = mailRepository,
            sendAfterSupplier = sendAfterSupplier,
            scheduledExecutorService = scheduledExecutorService,
            mimeMessage = newMimeMessage(),
            mailEntityGet = newMailEntityGet(),
        )
    }

    private fun newMimeMessage(): MimeMessage {
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8") // use multipart (true)

        addFilesTo(mimeMessageHelper)
        addSubjectTo(mimeMessageHelper)
        addToAddressTo(mimeMessageHelper)
        addFromAddressTo(mimeMessageHelper)
        addTextTo(mimeMessageHelper)

        return mimeMessage
    }

    private fun newMailEntityGet(): (isSuccess: Boolean) -> MailEntity = { isSuccess ->
        MailEntity(
            fromAddress = fromAddressSupplier(),
            fromName = fromNameSupplier(),
            toAddress = toAddressSupplier(),
            title = titleSupplier(),
            htmlTemplateName = htmlTemplateNameSupplier(),
            htmlTemplateParameters = htmlTemplateParametersSupplier().asJson(),
            isSuccess = isSuccess,
        )
    }

    private fun addToAddressTo(mimeMessageHelper: MimeMessageHelper) {
        mimeMessageHelper.setFrom(InternetAddress(fromAddressSupplier(), fromNameSupplier(), "UTF-8"))
    }

    private fun addFromAddressTo(mimeMessageHelper: MimeMessageHelper) {
        mimeMessageHelper.setTo(toAddressSupplier())
    }

    private fun addTextTo(mimeMessageHelper: MimeMessageHelper) {
        val html = htmlTemplateSupplier().apply(htmlTemplateParametersSupplier().asMap())
        mimeMessageHelper.setText(html, true)
    }

    private fun addFilesTo(mimeMessageHelper: MimeMessageHelper) {
        fileAttachmentDtoListSupplier().forEach {
            mimeMessageHelper.addAttachment(it.name, it.resultFile)
        }
    }

    private fun addSubjectTo(mimeMessageHelper: MimeMessageHelper) {
        val subject = MimeUtility.encodeText(
            appendedTitle(titleSupplier()),
            "UTF-8",
            "B"
        )
        mimeMessageHelper.setSubject(subject)
    }

    private fun appendedTitle(title: String): String {
        return if (count() > 0) {
            "$title (첨부파일 [${count()}]개, 전체크기 [${totalByteSize()}] bytes)"
        } else {
            title
        }
    }

    private fun totalByteSize(): Long {
        return fileAttachmentDtoListSupplier()
            .map { it.clientHttpResponse.headers.contentLength }
            .reduceOrNull { acc, size -> acc + size } ?: 0
    }

    private fun count(): Int {
        return fileAttachmentDtoListSupplier().size
    }
}