package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import java.util.concurrent.ScheduledExecutorService

class PostOffice(
    private val javaMailSender: JavaMailSender,
    private val getSendAfter: () -> SendAfter?,
    private val mailRepository: MailRepository,
    private val scheduledExecutorService: ScheduledExecutorService,
    private val mimeMessage: MimeMessage,
    private val mailEntityGet: (isSuccess: Boolean) -> MailEntity
    ) {

    fun newMailMessage(): MailMessage {
        val springJava = SpringJavaMailMessage(
            mimeMessage = mimeMessage,
            javaMailSender = javaMailSender,
            mailRepository = mailRepository,
            mailEntityGet = mailEntityGet,
        )
        val scheduled = ScheduledMailMessage(
            mailMessage = springJava,
            scheduledExecutorService = scheduledExecutorService,
            sendAfter = getSendAfter(),
        )
        return scheduled
    }
}
