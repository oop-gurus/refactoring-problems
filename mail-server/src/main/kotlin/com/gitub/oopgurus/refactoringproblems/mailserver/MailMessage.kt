package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage

data class MailMessage (
    val mimeMessage: MimeMessage,
    val htmlTemplateName: String,
    val htmlTemplateParameters: HtmlTemplateParameters,
    val title: String,
    val fromAddress: String,
    val fromName: String,
    val toAddress: String,
)
