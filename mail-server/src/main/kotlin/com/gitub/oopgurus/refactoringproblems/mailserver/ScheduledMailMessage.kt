package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScheduledMailMessage(
    private val mailMessage: MailMessage,
    private val scheduledExecutorService: ScheduledExecutorService,
    private val sendAfterSeconds: Long?
): MailMessage {
    override fun send(): MailSendResult {
        return if (sendAfterSeconds != null) {
            scheduledExecutorService.schedule(
                { mailMessage.send() },
                sendAfterSeconds,
                TimeUnit.SECONDS
            )

            MailSendResult()
        } else {
            mailMessage.send()
        }
    }
}
