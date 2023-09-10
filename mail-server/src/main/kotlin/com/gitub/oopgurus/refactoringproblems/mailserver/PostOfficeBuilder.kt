package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.util.StreamUtils
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ScheduledExecutorService

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
    private var sendAfterSecondsSupplier: () -> Long? = { throw IllegalStateException("getSendAfterSeconds is not set") }


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
        if (sendAfterSeconds != null && sendAfterSeconds <= 0) {
            throw RuntimeException("sendAfterSeconds는 0 이상이어야 합니다")
        }
        sendAfterSecondsSupplier = {sendAfterSeconds}
        return this
    }

    fun build(): PostOffice {
        return PostOffice(
            javaMailSender = javaMailSender,
            titleSupplier = titleSupplier,
            htmlTemplateSupplier = htmlTemplateSupplier,
            fromAddressSupplier = htmlTemplateNameSupplier,
            fromNameSupplier = fromNameSupplier,
            toAddressSupplier = toAddressSupplier,
            fileAttachmentDtoListSupplier = fileAttachmentDtoListSupplier,
            htmlTemplateNameSupplier = htmlTemplateNameSupplier,
            htmlTemplateParameters = htmlTemplateParametersSupplier(),
            mailRepository = mailRepository,
            sendAfterSecondsSupplier = sendAfterSecondsSupplier,
            scheduledExecutorService = scheduledExecutorService,
        )
    }
}