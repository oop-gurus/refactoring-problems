package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ScheduledExecutorService

class ScheduledMailMessage(
    private val sendAfter: SendAfter,
    private val scheduledExecutorService: ScheduledExecutorService,
    private val mailMessage: MailMessage,
) : MailMessage {

    override fun send(): MailSendResult {
        val delayedExecutor = CompletableFuture.delayedExecutor(
            sendAfter.amount(),
            sendAfter.unit(),
            scheduledExecutorService
        )
        val completionStage: CompletionStage<MailSendResult> = CompletableFuture.supplyAsync(
            { mailMessage.send() },
            delayedExecutor
        )

        return MailSendResultAsync(completionStage)
    }
}
