package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ScheduledExecutorService

class ScheduledMailMessage(
    private val scheduledExecutorService: ScheduledExecutorService,
    private val sendAfter: SendAfter?,
    private val mailMessage: MailMessage,
) : MailMessage {
    override fun send(): MailSendResult {
        return if (sendAfter == null) {
            sendNow()
        } else {
            sendAsync()
        }
    }

    private fun sendAsync(): MailSendResultAsync {
        val delayedExecutor = CompletableFuture.delayedExecutor(
            sendAfter!!.amount(),
            sendAfter!!.unit(),
            scheduledExecutorService
        )
        val completionStage: CompletionStage<MailSendResult> = CompletableFuture.supplyAsync(
            { sendNow() },
            delayedExecutor
        )

        return MailSendResultAsync(completionStage)
    }

    private fun sendNow() = mailMessage.send()
}
