package com.gitub.oopgurus.refactoringproblems.mailserver

import mu.KLogger
import mu.KotlinLogging
import java.util.concurrent.CompletionStage

interface MailSendResult {
    fun register()
}

class MailSendResultSuccess(
    private val mailRepository: MailRepository,
    private val mailEntity: MailEntity,
) : MailSendResult {
    override fun register() {
        mailRepository.save(mailEntity)
    }
}

class MailSendResultFailed(
    private val mailRepository: MailRepository,
    private val mailEntity: MailEntity,
) : MailSendResult {

    override fun register() {
        mailRepository.save(mailEntity)
    }
}

class MailSendResultAsync(
    private val completionStage: CompletionStage<MailSendResult>,
) : MailSendResult {

    override fun register() {
        completionStage.thenAccept { mailSendResult ->
            mailSendResult.register()
        }
    }
}

class LoggingMailSendResult(
    private val mailSendResult: MailSendResult,
    private val writeLogTo: (log: KLogger) -> Unit
): MailSendResult {
    private val log = KotlinLogging.logger {}

    override fun register() {
        mailSendResult.register()
        writeLogTo(log)
    }
}