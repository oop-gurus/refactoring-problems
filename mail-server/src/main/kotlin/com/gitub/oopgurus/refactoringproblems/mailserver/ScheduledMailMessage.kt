package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ScheduledThreadPoolExecutor

class ScheduledMailMessage(
    private val scheduledExecutorService: ScheduledThreadPoolExecutor,
    private val sendAfter: SendAfter?,
    private val mailMessage: MailMessage,
) : MailMessage {
    override fun send(): MailSendResult {
        return if (sendAfter != null) {
            val delayedExecutor = CompletableFuture.delayedExecutor(
                sendAfter.amount(),
                sendAfter.unit(),
                scheduledExecutorService
            )
            val completionStage: CompletionStage<MailSendResult> = CompletableFuture.supplyAsync(
                { mailMessage.send() },
                delayedExecutor
            )

            MailSendResultAsync(completionStage)
        } else {
            mailMessage.send()
        }
    }
}
