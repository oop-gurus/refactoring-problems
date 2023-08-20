package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Template

data class Mail(
    val title: String,

    val fromAddress: String,
    val fromName: String,
    val toAddress: String,

    val fileAttachments: List<FileAttachment>,
    val template: Template,
    val htmlTemplateParameters: Map<String, Any>,

    val sendAfterSeconds: Long?
) {

}