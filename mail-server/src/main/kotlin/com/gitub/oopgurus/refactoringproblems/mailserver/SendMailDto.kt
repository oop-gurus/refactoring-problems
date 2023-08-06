package com.gitub.oopgurus.refactoringproblems.mailserver

data class SendMailDto(
    val title: Title,
    val fromAddress: Address,
    val fromName: SenderName,
    val toAddress: DestinationAddress,
    val fileAttachments: List<FileAttachment>,
    val htmlTemplateName: HtmlTemplateName,
    val htmlTemplateParameters: Map<String, Any>,
    val sendAfterSeconds: Long?
)