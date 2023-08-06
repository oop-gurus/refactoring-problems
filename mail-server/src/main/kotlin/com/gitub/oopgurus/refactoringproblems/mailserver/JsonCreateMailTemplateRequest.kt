package com.gitub.oopgurus.refactoringproblems.mailserver

data class JsonCreateMailTemplateRequest(
    val name: String,
    val htmlBody: String
) {
    fun toCreateMailTemplateDto() = CreateMailTemplateDto(name, HtmlBody(htmlBody))
}
