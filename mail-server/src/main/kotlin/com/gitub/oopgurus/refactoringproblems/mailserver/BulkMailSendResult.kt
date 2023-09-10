package com.gitub.oopgurus.refactoringproblems.mailserver

class BulkMailSendResult(
    private val mailSendResults: List<MailSendResult>
) : MailSendResult {
    override fun register() {
        mailSendResults.forEach { it.register() }
    }

}
