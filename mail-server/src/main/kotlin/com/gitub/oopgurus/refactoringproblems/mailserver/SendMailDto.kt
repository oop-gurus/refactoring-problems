package com.gitub.oopgurus.refactoringproblems.mailserver

data class SendMailDto(
    val title: String,
    val fromAddress: Address,
    val fromName: String,
    val toAddress: Address,
    val fileAttachments: List<FileAttachment>,
    val htmlTemplateName: String,
    val htmlTemplateParameters: Map<String, Any>,
    val sendAfterSeconds: Long?
)