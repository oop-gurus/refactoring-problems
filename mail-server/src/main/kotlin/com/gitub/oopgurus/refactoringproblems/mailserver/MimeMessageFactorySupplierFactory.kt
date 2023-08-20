package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Template
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.util.StreamUtils
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream

class MimeMessageFactorySupplierFactory(
    private val javaMailSender: JavaMailSender,
    private val htmlTemplateParameters: HtmlTemplateParameters,
    private val fileAttachments: List<FileAttachment>,
    private val restTemplate: RestTemplate,
    private val titleSupplier: () -> String,
    private val htmlTemplateSupplier: () -> Template,
    private val fromAddressSupplier: () -> String,
    private val fromNameSupplier: () -> String,
    private val toAddressSupplier: () -> String,
) {
    fun create(): () -> MimeMessageFactory {
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
                }) ?: throw RuntimeException("파일 초기화 실패")

            if (fileAttachmentDto.resultFile.length() != fileAttachmentDto.clientHttpResponse.headers.contentLength) {
                throw RuntimeException("파일 크기 불일치")
            }

            if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(fileAttachmentDto.clientHttpResponse.headers.contentLength)) {
                throw RuntimeException("파일 크기 초과")
            }

            fileAttachmentDto
        }

        return {
            MimeMessageFactory(
                javaMailSender = javaMailSender,
                titleSupplier = titleSupplier,
                htmlTemplateSupplier = htmlTemplateSupplier,
                htmlTemplateParameters = htmlTemplateParameters,
                fromAddressSupplier = fromAddressSupplier,
                fromNameSupplier = fromNameSupplier,
                toAddressSupplier = toAddressSupplier,
                fileAttachmentDtoList = fileAttachmentDtoList,
            )
        }
    }
}