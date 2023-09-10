package com.gitub.oopgurus.refactoringproblems.mailserver

class BulkMailMessage(
    private val mailMessages: List<MailMessage>
): MailMessage {
    override fun send(): MailSendResult {
        return BulkMailSendResult(mailMessages.map { it.send() })
    }
}