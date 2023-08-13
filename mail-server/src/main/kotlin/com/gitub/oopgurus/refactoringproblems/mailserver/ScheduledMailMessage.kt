package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.ScheduledExecutorService

class ScheduledMailMessage(
    private val scheduledExecutorService: ScheduledExecutorService,
    private val sendAfter: SendAfter?,
    private val mailMessage: MailMessage,
) : MailMessage {
    override fun send(): MailSendResult {
        return if (sendAfter != null) {
            scheduledExecutorService.schedule(
                { mailMessage.send() },
                sendAfter.amount(),
                sendAfter.unit()
            )

            // TODO(jaeeun): async 처리할 수 있도록 수정해야함
            MailSendResult()
        } else {
            mailMessage.send()
        }
    }
}
