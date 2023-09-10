package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ScheduledMailMessage(
    private val mailMessage: MailMessage,
    private val scheduledExecutorService: ScheduledThreadPoolExecutor,
    private val sendAfter: SendAfter?
): MailMessage {
    override fun send(): MailSendResult {
        return if (sendAfter != null) {
            val delayedExecutor = CompletableFuture.delayedExecutor(
                sendAfter.amount,
                sendAfter.unit,
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
