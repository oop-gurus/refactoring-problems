package com.gitub.oopgurus.refactoringproblems.mailserver

class MailSendResultFactory(
    private val mailRepository: MailRepository,
    private val mailEntityGet: (isSuccess: Boolean) -> MailEntity
) {
    fun success(): MailSendResult {
        return MailSendResultSuccess(
            mailRepository = mailRepository,
            mailEntity = mailEntityGet(true),
        )
    }

    fun failed(exception: Exception): MailSendResult {
        return MailSendResultFailed(
            mailRepository = mailRepository,
            mailEntity = mailEntityGet(false),
            exception = exception,
        )
    }
}