package com.gitub.oopgurus.refactoringproblems.mailserver

data class JsonSendMailRequest(
    val title: String,
    val fromAddress: String,
    val fromName: String,
    val toAddress: String,
    val fileAttachments: List<FileAttachment>,
    val htmlTemplateName: String,
    val htmlTemplateParameters: Map<String, Any>,
    val sendAfterSeconds: Long?
) {
    fun toSendMailDto() = SendMailDto(
        title = Title(title),
        fromAddress = Address(fromAddress),
        fromName = SenderName(fromName),
        toAddress = DestinationAddress(toAddress),
        fileAttachments = fileAttachments,
        htmlTemplateName = HtmlTemplateName(htmlTemplateName),
        htmlTemplateParameters = htmlTemplateParameters,
        sendAfterSeconds = sendAfterSeconds
    )
}

