package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import mu.KotlinLogging
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.util.StreamUtils
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream

class MimeMessageCreator(
    private val mailTemplateRepository: MailTemplateRepository,
    private val javaMailSender: JavaMailSender,
    private val restTemplate: RestTemplate,
) {

    private val log = KotlinLogging.logger {}
    private val handlebars = Handlebars().also {
        it.registerHelperMissing(Helper<Any> { context, options ->
            throw IllegalArgumentException("누락된 파라메터 발생: [${options.helperName}]")
        })
    }

    fun create(sendMailDto: SendMailDto): MimeMessage {
        val htmlTemplate = mailTemplateRepository.findByName(sendMailDto.htmlTemplateName.value)
            ?: throw RuntimeException("템플릿이 존재하지 않습니다: [${sendMailDto.htmlTemplateName}]")
        val template: Template = handlebars.compileInline(htmlTemplate.htmlBody)
        val html = template.apply(sendMailDto.htmlTemplateParameters)
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()

        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8") // use multipart (true)
        mimeMessageHelper.setText(html, true)
        mimeMessageHelper.setFrom(InternetAddress(sendMailDto.fromAddress.value, sendMailDto.fromName.value, "UTF-8"))
        mimeMessageHelper.setTo(sendMailDto.toAddress.value)

        val fileResults = sendMailDto.fileAttachments.mapIndexed { index, attachment ->
            val result = restTemplate.execute(
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
                }) ?: throw RuntimeException("파일 초기화 실패")

            result
        }
        fileResults.forEach {
            val fileSystemResource: FileSystemResource = FileSystemResource(File(it.resultFile.absolutePath))
            mimeMessageHelper.addAttachment(
                MimeUtility.encodeText(
                    it.name,
                    "UTF-8",
                    "B"
                ), fileSystemResource
            )
        }

        var postfixTitle = ""
        if (fileResults.isNotEmpty()) {
            val totalSize = fileResults
                .map { it.clientHttpResponse.headers.contentLength }
                .reduceOrNull { acc, size -> acc + size } ?: 0
            postfixTitle = " (첨부파일 [${fileResults.size}]개, 전체크기 [$totalSize bytes])"
        }
        mimeMessageHelper.setSubject(
            MimeUtility.encodeText(
                sendMailDto.title.value + postfixTitle,
                "UTF-8",
                "B"
            )
        ) // Base64 encoding

        return mimeMessage
    }
}