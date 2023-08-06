package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import mu.KotlinLogging
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Component
class MailService(
    private val javaMailSender: JavaMailSender,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailRepository: MailRepository,
    private val postOfficeBuilderFactory: PostOfficeBuilderFactory,
) {

    private val log = KotlinLogging.logger {}
    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    data class FileAttachmentDto(
        val resultFile: File,
        val name: String,
        val clientHttpResponse: ClientHttpResponse,
    )

    fun send(sendMailDtos: List<SendMailDto>) {
        sendMailDtos.forEach {
            sendSingle(it)
        }
    }

    class HtmlTemplateParameters(
        private val holder: Map<String, Any>,
        private val objectMapper: ObjectMapper,
    ) {
        fun asMap(): Map<String, Any> {
            return HashMap(holder)
        }

        fun asJson(): String {
            return objectMapper.writeValueAsString(holder)
        }
    }

    class PostOffice(
        private val javaMailSender: JavaMailSender,
        private val getTitle: () -> String,
        private val getHtmlTemplate: () -> Template,
        private val getHtmlTemplateName: () -> String,
        private val getHtmlTemplateParameters: () -> HtmlTemplateParameters,
        private val getFromAddress: () -> String,
        private val getFromName: () -> String,
        private val getToAddress: () -> String,
        private val getFileAttachmentDtoList: () -> List<FileAttachmentDto>,
    ) {
        fun newMailMessage(): MailMessage {
            val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8") // use multipart (true)

            addFilesTo(mimeMessageHelper)
            addSubjectTo(mimeMessageHelper)
            addToAddressTo(mimeMessageHelper)
            addFromAddressTo(mimeMessageHelper)
            addTextTo(mimeMessageHelper)

            return MailMessage(
                mimeMessage = mimeMessage,
                htmlTemplateName = getHtmlTemplateName(),
                htmlTemplateParameters = getHtmlTemplateParameters(),
                title = getTitle(),
                fromAddress = getFromAddress(),
                fromName = getFromName(),
                toAddress = getToAddress(),
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

    private fun sendSingle(sendMailDto: SendMailDto) {
        val postOffice = postOfficeBuilderFactory.create()
            .toAddress(sendMailDto.toAddress)
            .fromName(sendMailDto.fromName)
            .fromAddress(sendMailDto.fromAddress)
            .title(sendMailDto.title)
            .htmlTemplateName(sendMailDto.htmlTemplateName)
            .htmlTemplateParameters(sendMailDto.htmlTemplateParameters)
            .fileAttachments(sendMailDto.fileAttachments)
            .build()

        val mailMessage = postOffice.newMailMessage()
        try {
            if (sendMailDto.sendAfterSeconds != null) {
                scheduledExecutorService.schedule(
                    {
                        javaMailSender.send(mailMessage.mimeMessage())
                        mailRepository.save(
                            MailEntity(
                                fromAddress = mailMessage.fromAddress(),
                                fromName = mailMessage.fromName(),
                                toAddress = mailMessage.toAddress(),
                                title = mailMessage.title(),
                                htmlTemplateName = mailMessage.htmlTemplateName(),
                                htmlTemplateParameters = mailMessage.htmlTemplateParameters().asJson(),
                                isSuccess = true,
                            )
                        )
                        log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
                    },
                    sendMailDto.sendAfterSeconds,
                    TimeUnit.SECONDS
                )

            } else {
                javaMailSender.send(mailMessage.mimeMessage())
                mailRepository.save(
                    MailEntity(
                        fromAddress = mailMessage.fromAddress(),
                        fromName = mailMessage.fromName(),
                        toAddress = mailMessage.toAddress(),
                        title = mailMessage.title(),
                        htmlTemplateName = mailMessage.htmlTemplateName(),
                        htmlTemplateParameters = mailMessage.htmlTemplateParameters().asJson(),
                        isSuccess = true,
                    )
                )
                log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
            }
        } catch (e: Exception) {
            mailRepository.save(
                MailEntity(
                    fromAddress = mailMessage.fromAddress(),
                    fromName = mailMessage.fromName(),
                    toAddress = mailMessage.toAddress(),
                    title = mailMessage.title(),
                    htmlTemplateName = mailMessage.htmlTemplateName(),
                    htmlTemplateParameters = mailMessage.htmlTemplateParameters().asJson(),
                    isSuccess = false,
                )
            )
            log.error(e) { "MailServiceImpl.sendMail() :: FAILED" }
        }
    }

    fun creatMailTemplate(createMailTemplateDtos: List<CreateMailTemplateDto>) {
        createMailTemplateDtos.forEach {
            if (it.htmlBody.isBlank()) {
                throw IllegalArgumentException("htmlBody is blank")
            }
            mailTemplateRepository.save(
                MailTemplateEntity(
                    name = it.name,
                    htmlBody = it.htmlBody,
                )
            )
        }
    }
}

@Component
class PostOfficeBuilderFactory(
    private val mailSpamService: MailSpamService,
    private val mailTemplateRepository: MailTemplateRepository,
    private val handlebars: Handlebars,
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate,
    private val javaMailSender: JavaMailSender,
    ) {

    fun create(): PostOfficeBuilder {
        return PostOfficeBuilder(
            mailSpamService = mailSpamService,
            mailTemplateRepository = mailTemplateRepository,
            handlebars = handlebars,
            objectMapper = objectMapper,
            restTemplate = restTemplate,
            javaMailSender = javaMailSender,
        )
    }
}

class PostOfficeBuilder(
    private val mailSpamService: MailSpamService,
    private val mailTemplateRepository: MailTemplateRepository,
    private val handlebars: Handlebars,
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate,
    private val javaMailSender: JavaMailSender,
) {
    private var getToAddress: () -> String = { throw IllegalStateException("getToAddress is not set") }
    private var getFromAddress: () -> String = { throw IllegalStateException("getToAddress is not set") }
    private var getHtmlTemplateName: () -> String = { throw IllegalStateException("getToAddress is not set") }
    private var getHtmlTemplate: () -> Template = { throw IllegalStateException("getHtmlTemplate is not set") }
    private var getTitle: () -> String = { throw IllegalStateException("getTitle is not set") }
    private var getFromName: () -> String = { throw IllegalStateException("getTitle is not set") }
    private var getHtmlTemplateParameters: () -> MailService.HtmlTemplateParameters =
        { throw IllegalStateException("getTitle is not set") }

    private var getFileAttachmentDtoList: () -> List<MailService.FileAttachmentDto> = { throw IllegalStateException("getTitle is not set") }


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
            MailService.HtmlTemplateParameters(
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

                    MailService.FileAttachmentDto(
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

    fun build(): MailService.PostOffice {
        return MailService.PostOffice(
            javaMailSender = javaMailSender,
            getTitle = getTitle,
            getHtmlTemplate = getHtmlTemplate,
            getHtmlTemplateName = getHtmlTemplateName,
            getHtmlTemplateParameters = getHtmlTemplateParameters,
            getFromAddress = getFromAddress,
            getFromName = getFromName,
            getToAddress = getToAddress,
            getFileAttachmentDtoList = getFileAttachmentDtoList
        )
    }
}

class MailMessage(
    private val mimeMessage: MimeMessage,
    private val htmlTemplateName: String,
    private val htmlTemplateParameters: MailService.HtmlTemplateParameters,
    private val title: String,
    private val fromAddress: String,
    private val fromName: String,
    private val toAddress: String,
) {
    fun mimeMessage(): MimeMessage {
        return mimeMessage
    }

    fun fromAddress(): String {
        return fromAddress
    }

    fun fromName(): String {
        return fromName
    }

    fun toAddress(): String {
        return toAddress
    }

    fun title(): String {
        return title
    }

    fun htmlTemplateName(): String {
        return htmlTemplateName
    }

    fun htmlTemplateParameters(): MailService.HtmlTemplateParameters {
        return htmlTemplateParameters
    }
}
