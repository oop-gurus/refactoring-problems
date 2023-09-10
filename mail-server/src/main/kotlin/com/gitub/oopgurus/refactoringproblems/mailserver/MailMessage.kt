package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

data class MailMessage (
    private val mimeMessage: MimeMessage,
    private val htmlTemplateName: String,
    private val htmlTemplateParameters: HtmlTemplateParameters,
    private val title: String,
    private val fromAddress: String,
    private val fromName: String,
    private val toAddress: String,
    private val sendAfterSeconds: Long?,
    private val javaMailSender: JavaMailSender,
    private val mailRepository: MailRepository,
    private val scheduledExecutorService: ScheduledExecutorService,
) {
    private val log = KotlinLogging.logger {}

    fun send(): MailSendResult {
        try {
            if (sendAfterSeconds != null) {
                scheduledExecutorService.schedule(
                    {
                        javaMailSender.send(mimeMessage)
                        mailRepository.save(
                            MailEntity(
                                fromAddress = fromAddress,
                                fromName = fromName,
                                toAddress = toAddress,
                                title = title,
                                htmlTemplateName = htmlTemplateName,
                                htmlTemplateParameters = htmlTemplateParameters.asJson(),
                                isSuccess = true,
                            )
                        )
                        log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
                    },
                    sendAfterSeconds,
                    TimeUnit.SECONDS
                )

            } else {
                javaMailSender.send(mimeMessage)
                mailRepository.save(
                    MailEntity(
                        fromAddress = fromAddress,
                        fromName = fromName,
                        toAddress = toAddress,
                        title = title,
                        htmlTemplateName = htmlTemplateName,
                        htmlTemplateParameters = htmlTemplateParameters.asJson(),
                        isSuccess = true,
                    )
                )
                log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
            }
        } catch (e: Exception) {
            mailRepository.save(
                MailEntity(
                    fromAddress = fromAddress,
                    fromName = fromName,
                    toAddress = toAddress,
                    title = title,
                    htmlTemplateName = htmlTemplateName,
                    htmlTemplateParameters = htmlTemplateParameters.asJson(),
                    isSuccess = false,
                )
            )
            log.error(e) { "MailServiceImpl.sendMail() :: FAILED" }
        }

        return MailSendResult()
    }
}
