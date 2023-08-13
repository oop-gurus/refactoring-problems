package com.gitub.oopgurus.refactoringproblems.mailserver

import java.lang.Exception

class MailSendResultFactory(
    private val mailRepository: MailRepository,
    private val mailEntityGet: (isSuccess: Boolean) -> MailEntity,
) {

    fun success(): MailSendResult {
        val success = MailSendResultSuccess(
            mailRepository = mailRepository,
            mailEntity = mailEntityGet(true),
        )

        val logging = LoggingMailSendResult(
            mailSendResult = success,
            writeLogTo = { it.info { "MailSendResult :: SUCCESS" } },
        )
        return logging
    }

    fun failed(exception: Exception): MailSendResult {
        val failed = MailSendResultFailed(
            mailRepository = mailRepository,
            mailEntity = mailEntityGet(false),
        )
        val logging = LoggingMailSendResult(
            mailSendResult = failed,
            writeLogTo = { it.error(exception) { "MailSendResult :: FAILED" } },
        )
        return logging
    }
}
