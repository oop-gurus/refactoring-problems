package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

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


    private fun mimeMessage(): MimeMessage {
        return mimeMessage
    }

    private fun fromAddress(): String {
        return fromAddress
    }

    private fun fromName(): String {
        return fromName
    }

    private fun toAddress(): String {
        return toAddress
    }

    private fun title(): String {
        return title
    }

    private fun htmlTemplateName(): String {
        return htmlTemplateName
    }

    private fun htmlTemplateParameters(): HtmlTemplateParameters {
        return htmlTemplateParameters
    }

    override fun send(): MailSendResult {
        try {
            sendNow()
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
                fromAddress = fromAddress(),
                fromName = fromName(),
                toAddress = toAddress(),
                title = title(),
                htmlTemplateName = htmlTemplateName(),
                htmlTemplateParameters = htmlTemplateParameters().asJson(),
                isSuccess = false,
            )
        )
    }

    private fun saveSuccess() {
        mailRepository.save(
            MailEntity(
                fromAddress = fromAddress(),
                fromName = fromName(),
                toAddress = toAddress(),
                title = title(),
                htmlTemplateName = htmlTemplateName(),
                htmlTemplateParameters = htmlTemplateParameters().asJson(),
                isSuccess = true,
            )
        )
    }

    private fun sendNow() {
        javaMailSender.send(mimeMessage())
    }
}
