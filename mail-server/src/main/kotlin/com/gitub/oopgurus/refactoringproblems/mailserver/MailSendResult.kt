package com.gitub.oopgurus.refactoringproblems.mailserver

import mu.KotlinLogging
import java.util.concurrent.CompletionStage

interface MailSendResult {
    fun register()
}

class MailSendResultSuccess(
    private val mailRepository: MailRepository,
    private val mailEntity: MailEntity,
) : MailSendResult {
    private val log = KotlinLogging.logger {}

    override fun register() {
        mailRepository.save(mailEntity)
        log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
    }
}

class MailSendResultFailed(
    private val mailRepository: MailRepository,
    private val mailEntity: MailEntity,
    private val exception: Exception,
) : MailSendResult {
    private val log = KotlinLogging.logger {}

    override fun register() {
        mailRepository.save(mailEntity)
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
