package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage

class MailMessage(
    private val mimeMessage: MimeMessage,
    private val htmlTemplateName: String,
    private val htmlTemplateParameters: HtmlTemplateParameters,
    private val title: String,
    private val fromAddress: String,
    private val fromName: String,
    private val toAddress: String,
) {
    fun mimeMessage(): MimeMessage {
        return mimeMessage
    }

    fun fromAddress(): String {
        return fromAddress
    }

    fun fromName(): String {
        return fromName
    }

    fun toAddress(): String {
        return toAddress
    }

    fun title(): String {
        return title
    }

    fun htmlTemplateName(): String {
        return htmlTemplateName
    }

    fun htmlTemplateParameters(): HtmlTemplateParameters {
        return htmlTemplateParameters
    }

    fun send(): MailSendResult {
        TODO("Not yet implemented")
    }
}
