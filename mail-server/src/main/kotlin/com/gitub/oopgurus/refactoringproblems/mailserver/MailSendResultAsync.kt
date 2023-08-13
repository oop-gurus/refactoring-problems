package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.CompletionStage

class MailSendResultAsync(
    private val completionStage: CompletionStage<MailSendResult>,
) : MailSendResult {

    override fun register() {
        completionStage.thenAccept { mailSendResult ->
            mailSendResult.register()
        }
    }
}
