package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender

class SpringJavaMailMessage(
    private val mimeMessage: MimeMessage,
    private val htmlTemplateName: String,
    private val htmlTemplateParameters: HtmlTemplateParameters,
    private val title: String,
    private val fromAddress: String,
    private val fromName: String,
    private val toAddress: String,
    private val javaMailSender: JavaMailSender,
    private val mailRepository: MailRepository,
) : MailMessage {

    private val log = KotlinLogging.logger {}

    override fun send(): MailSendResult {
        try {
            javaMailSender.send(mimeMessage)
            saveSuccess()
            log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
        } catch (e: Exception) {
            saveFailed()
            log.error(e) { "MailServiceImpl.sendMail() :: FAILED" }
        }
        return MailSendResult()
    }

    private fun saveFailed() {
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
    }

    private fun saveSuccess() {
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
    }
}
