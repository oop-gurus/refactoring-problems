package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage

data class MailMessage (
    val mimeMessage: MimeMessage,
    private val htmlTemplateName: String,
    private val htmlTemplateParameters: HtmlTemplateParameters,
    private val title: String,
    private val fromAddress: String,
    private val fromName: String,
    private val toAddress: String,
)
