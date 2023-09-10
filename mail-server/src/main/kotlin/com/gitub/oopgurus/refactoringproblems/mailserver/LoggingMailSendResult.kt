package com.gitub.oopgurus.refactoringproblems.mailserver

import mu.KLogger
import mu.KotlinLogging

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