package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
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
    private val restTemplate: RestTemplate,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailRepository: MailRepository,
    private val objectMapper: ObjectMapper,
    private val mailSpamService: MailSpamService,
) {

    private val log = KotlinLogging.logger {}
    private val handlebars = Handlebars().also {
        it.registerHelperMissing(Helper<Any> { context, options ->
            throw IllegalArgumentException("누락된 파라메터 발생: [${options.helperName}]")
        })
    }
    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    data class FileAttachmentDto(
        val resultFile: File,
        val name: String,
        val clientHttpResponse: ClientHttpResponse,
    )

    fun send(sendMailDtoList: List<SendMailDto>) {
        sendMailDtoList.forEach {
            sendSingle(it)
        }
    }

    private fun sendSingle(sendMailDto: SendMailDto) {
        mailSpamService.needBlockByRecentSuccess(sendMailDto.toAddress.value).let {
            if (it) {
                throw RuntimeException("최근 메일 발송 실패로 인한 차단")
            }
        }

        val htmlTemplate = mailTemplateRepository.findByName(sendMailDto.htmlTemplateName.value)
            ?: throw RuntimeException("템플릿이 존재하지 않습니다: [${sendMailDto.htmlTemplateName}]")
        val template: Template = handlebars.compileInline(htmlTemplate.htmlBody)
        val html = template.apply(sendMailDto.htmlTemplateParameters)
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()

        try {
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
                    })

                if (result == null) {
                    throw RuntimeException("파일 초기화 실패")
                }
                if (result.resultFile.length() != result.clientHttpResponse.headers.contentLength) {
                    throw RuntimeException("파일 크기 불일치")
                }
                if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(result.clientHttpResponse.headers.contentLength)) {
                    throw RuntimeException("파일 크기 초과")
                }
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


            if (sendMailDto.sendAfterSeconds != null) {
                scheduledExecutorService.schedule(
                    {
                        javaMailSender.send(mimeMessage)
                        mailRepository.save(
                            MailEntity(
                                fromAddress = sendMailDto.fromAddress.value,
                                fromName = sendMailDto.fromName.value,
                                toAddress = sendMailDto.toAddress.value,
                                title = sendMailDto.title.value,
                                htmlTemplateName = sendMailDto.htmlTemplateName.value,
                                htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                                isSuccess = true,
                            )
                        )
                        log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
                    },
                    sendMailDto.sendAfterSeconds,
                    TimeUnit.SECONDS
                )

            } else {
                javaMailSender.send(mimeMessage)
                mailRepository.save(
                    MailEntity(
                        fromAddress = sendMailDto.fromAddress.value,
                        fromName = sendMailDto.fromName.value,
                        toAddress = sendMailDto.toAddress.value,
                        title = sendMailDto.title.value,
                        htmlTemplateName = sendMailDto.htmlTemplateName.value,
                        htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                        isSuccess = true,
                    )
                )
                log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
            }
        } catch (e: Exception) {
            mailRepository.save(
                MailEntity(
                    fromAddress = sendMailDto.fromAddress.value,
                    fromName = sendMailDto.fromName.value,
                    toAddress = sendMailDto.toAddress.value,
                    title = sendMailDto.title.value,
                    htmlTemplateName = sendMailDto.htmlTemplateName.value,
                    htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                    isSuccess = false,
                )
            )
            log.error(e) { "MailServiceImpl.sendMail() :: FAILED" }
        }
    }
}
