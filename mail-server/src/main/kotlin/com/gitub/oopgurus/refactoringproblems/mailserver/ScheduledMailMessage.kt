package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScheduledMailMessage(
    private val scheduledExecutorService: ScheduledExecutorService,
    private val sendAfterSeconds: Long?,
    private val mailMessage: MailMessage,
) : MailMessage {
    override fun send(): MailSendResult {
        return if (sendAfterSeconds != null) {
            scheduledExecutorService.schedule(
                { mailMessage.send() },
                sendAfterSeconds,
                TimeUnit.SECONDS
            )

            // TODO(jaeeun): async 처리할 수 있도록 수정해야함
            MailSendResult()
        } else {
            mailMessage.send()
        }
    }
}
