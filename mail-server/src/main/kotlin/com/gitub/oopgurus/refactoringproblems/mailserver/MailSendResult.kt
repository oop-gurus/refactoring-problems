package com.gitub.oopgurus.refactoringproblems.mailserver

import mu.KotlinLogging
import java.util.concurrent.CompletionStage

interface MailSendResult {
    fun register()
}

class MailSendResultSuccess(
    private val mailRepository: MailRepository,
    private val mailEntityGet: (isSuccess: Boolean) -> MailEntity,
) : MailSendResult {
    private val log = KotlinLogging.logger {}

    override fun register() {
        mailRepository.save(mailEntityGet(true))
        log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
    }
}

class MailSendResultFailed(
    private val mailRepository: MailRepository,
    private val mailEntityGet: (isSuccess: Boolean) -> MailEntity,
    private val exception: Exception,
) : MailSendResult {
    private val log = KotlinLogging.logger {}

    override fun register() {
        mailRepository.save(mailEntityGet(false))
        log.error(exception) { "MailServiceImpl.sendMail() :: FAILED" }
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
