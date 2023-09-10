package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender

data class SpringJavaMailMessage(
    private val mimeMessage: MimeMessage,
    private val javaMailSender: JavaMailSender,
    private val mailSendResultFactory: MailSendResultFactory
): MailMessage {
    override fun send(): MailSendResult {
        return try {
            javaMailSender.send(mimeMessage)
            mailSendResultFactory.success()
        } catch (exception: Exception) {
            mailSendResultFactory.failed(exception)
        }
    }
}
