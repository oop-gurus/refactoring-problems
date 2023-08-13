package com.gitub.oopgurus.refactoringproblems.mailserver

class MailSendResultFailed(
    private val mailRepository: MailRepository,
    private val mailEntity: MailEntity,
) : MailSendResult {
    override fun register() {
        mailRepository.save(mailEntity)
    }
}
