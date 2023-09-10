package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import java.util.concurrent.ScheduledExecutorService

class PostOffice(
    private val javaMailSender: JavaMailSender,
    private val mimeMessage: MimeMessage,
    private val mailEntityGet: (isSuccess: Boolean) -> MailEntity
    private val sendAfterSupplier: () -> SendAfter?,
    private val mailRepository: MailRepository,
    private val scheduledExecutorService: ScheduledExecutorService,
) {
    fun newMailMessage(): MailMessage {
        val springJavaMailMessage = SpringJavaMailMessage(
            mimeMessage = mimeMessage,
            mailEntityGet = mailEntityGet,
            javaMailSender = javaMailSender,
            mailRepository = mailRepository,
        )

        return ScheduledMailMessage(
            mailMessage = springJavaMailMessage,
            scheduledExecutorService = scheduledExecutorService,
            sendAfter = sendAfterSupplier(),
        )
    }
}