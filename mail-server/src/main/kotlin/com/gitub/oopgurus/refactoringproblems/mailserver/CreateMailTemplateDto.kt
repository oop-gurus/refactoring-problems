package com.gitub.oopgurus.refactoringproblems.mailserver

data class CreateMailTemplateDto(
        val name: String,
        val htmlBody: HtmlBody,
)
