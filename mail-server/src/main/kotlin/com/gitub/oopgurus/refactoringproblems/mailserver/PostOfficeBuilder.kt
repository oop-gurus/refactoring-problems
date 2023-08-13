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
import org.springframework.util.StreamUtils
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
    private var getToAddress: () -> String = { throw IllegalStateException("getToAddress is not set") }
    private var getFromAddress: () -> String = { throw IllegalStateException("getFromAddress is not set") }
    private var getHtmlTemplateName: () -> String = { throw IllegalStateException("getHtmlTemplateName is not set") }
    private var getHtmlTemplate: () -> Template = { throw IllegalStateException("getHtmlTemplate is not set") }
    private var getTitle: () -> String = { throw IllegalStateException("getTitle is not set") }
    private var getFromName: () -> String = { throw IllegalStateException("getFromName is not set") }
    private var getHtmlTemplateParameters: () -> HtmlTemplateParameters =
        { throw IllegalStateException("getHtmlTemplateParameters is not set") }

    private var getFileAttachmentDtoList: () -> List<FileAttachmentDto> =
        { throw IllegalStateException("getFileAttachmentDtoList is not set") }
    private var getSendAfter: () -> SendAfter? = { throw IllegalStateException("getSendAfter is not set") }


    fun toAddress(toAddress: String): PostOfficeBuilder {
        getToAddress = when {
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
        getFromAddress = when {
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
        getHtmlTemplateName = when {
            htmlTemplateName.isBlank() -> {
                { throw RuntimeException("템플릿 이름이 비어있습니다") }
            }

            else -> {
                { htmlTemplateName }
            }
        }

        val htmlTemplate = mailTemplateRepository.findByName(htmlTemplateName)
        getHtmlTemplate = when {
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
        getTitle = when {
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
        getFromName = when {
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
        getHtmlTemplateParameters = {
            HtmlTemplateParameters(
                holder = htmlTemplateParameters,
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
                    StreamUtils.copy(clientHttpResponse.body, FileOutputStream(tempFile))

                    FileAttachmentDto(
                        resultFile = tempFile,
                        name = attachment.name,
                        clientHttpResponse = clientHttpResponse
                    )
                })

            if (fileAttachmentDto == null) {
                throw RuntimeException("파일 초기화 실패")
            }
            if (fileAttachmentDto.resultFile.length() != fileAttachmentDto.clientHttpResponse.headers.contentLength) {
                throw RuntimeException("파일 크기 불일치")
            }
            if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(fileAttachmentDto.clientHttpResponse.headers.contentLength)) {
                throw RuntimeException("파일 크기 초과")
            }
            fileAttachmentDto
        }

        getFileAttachmentDtoList = { fileAttachmentDtoList }
        return this
    }

    fun sendAfterSeconds(sendAfterSeconds: Long?): PostOfficeBuilder {
        if (sendAfterSeconds == null) {
            getSendAfter = { null }
            return this
        }

        if (sendAfterSeconds <= 0) {
            throw RuntimeException("sendAfterSeconds는 0 이상이어야 합니다")
        }
        getSendAfter = {
            SendAfter(
                amount = sendAfterSeconds,
                unit = TimeUnit.SECONDS,
            )
        }
        return this
    }

    fun build(): MailMessage {
        val mailSendResultFactory = MailSendResultFactory(
            mailRepository = mailRepository,
            mailEntityGet = newMailEntityGet(),
        )
        val springJava = SpringJavaMailMessage(
            javaMailSender = javaMailSender,
            mimeMessage = newMimeMessage(),
            mailSendResultFactory = mailSendResultFactory,
        )
        val scheduled = ScheduledMailMessage(
            mailMessage = springJava,
            scheduledExecutorService = scheduledExecutorService,
            sendAfter = getSendAfter(),
        )
        return scheduled
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
            fromAddress = getToAddress(),
            fromName = getFromName(),
            toAddress = getFromAddress(),
            title = getTitle(),
            htmlTemplateName = getHtmlTemplateName(),
            htmlTemplateParameters = getHtmlTemplateParameters().asJson(),
            isSuccess = isSuccess,
        )
    }

    private fun addFilesTo(mimeMessageHelper: MimeMessageHelper) {
        getFileAttachmentDtoList().forEach {
            mimeMessageHelper.addAttachment(it.name, it.resultFile)
        }
    }

    private fun addSubjectTo(mimeMessageHelper: MimeMessageHelper) {
        val subject = MimeUtility.encodeText(
            appendedTitle(getTitle()),
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
        return getFileAttachmentDtoList()
            .map { it.clientHttpResponse.headers.contentLength }
            .reduceOrNull { acc, size -> acc + size } ?: 0
    }

    private fun count(): Int {
        return getFileAttachmentDtoList().size
    }

    private fun addToAddressTo(mimeMessageHelper: MimeMessageHelper) {
        mimeMessageHelper.setFrom(InternetAddress(getFromAddress(), getFromName(), "UTF-8"))
    }

    private fun addFromAddressTo(mimeMessageHelper: MimeMessageHelper) {
        mimeMessageHelper.setTo(getToAddress())
    }

    private fun addTextTo(mimeMessageHelper: MimeMessageHelper) {
        val html = getHtmlTemplate().apply(getHtmlTemplateParameters().asMap())
        mimeMessageHelper.setText(html, true)
    }
}
