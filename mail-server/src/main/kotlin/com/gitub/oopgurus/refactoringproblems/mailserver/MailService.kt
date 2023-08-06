package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Template
import jakarta.mail.internet.MimeMessage
import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
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
    private val mimeMessageCreator: MimeMessageCreator,
) {

    private val log = KotlinLogging.logger {}
    private val handlebars = Handlebars().also {
        it.registerHelperMissing(Helper<Any> { context, options ->
            throw IllegalArgumentException("누락된 파라메터 발생: [${options.helperName}]")
        })
    }
    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

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
            mimeMessageCreator.create(sendMailDto)

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
