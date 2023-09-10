package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScheduledMailMessage(
    private val mailMessage: MailMessage,
    private val scheduledExecutorService: ScheduledExecutorService,
    private val sendAfter: SendAfter?
): MailMessage {
    override fun send(): MailSendResult {
        return if (sendAfter != null) {
            scheduledExecutorService.schedule(
                { mailMessage.send() },
                sendAfter.amount,
                sendAfter.unit
            )

            MailSendResult()
        } else {
            mailMessage.send()
        }
    }
}
