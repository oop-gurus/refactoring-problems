package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender

class SpringJavaMailMessage(
    private val mimeMessage: MimeMessage,
    private val javaMailSender: JavaMailSender,
    private val mailRepository: MailRepository,
    private val mailEntityGet: (isSuccess: Boolean) -> MailEntity
) : MailMessage {

    override fun send(): MailSendResult {
        return try {
            javaMailSender.send(mimeMessage)

            MailSendResultSuccess(
                mailRepository = mailRepository,
                mailEntityGet = mailEntityGet,
            )
        } catch (exception: Exception) {
            MailSendResultFailed(
                mailRepository = mailRepository,
                mailEntityGet = mailEntityGet,
                exception = exception,
            )
        }
    }
}
