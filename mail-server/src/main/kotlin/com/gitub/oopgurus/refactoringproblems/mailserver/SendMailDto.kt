package com.gitub.oopgurus.refactoringproblems.mailserver

data class SendMailDto(
    val title: String,
    val fromAddress: String,
    val fromName: String,
    val toAddress: String,
    val fileAttachments: List<FileAttachment>,
    val htmlTemplateName: String,
    val htmlTemplateParameters: Map<String, Any>,
    val sendAfterSeconds: Long?
)

data class FileAttachment(
    val name: String,
    val url: String,
)
