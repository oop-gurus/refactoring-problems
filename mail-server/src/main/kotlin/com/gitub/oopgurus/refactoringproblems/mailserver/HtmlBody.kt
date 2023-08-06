package com.gitub.oopgurus.refactoringproblems.mailserver

data class HtmlBody(
        val contents: String,
) {
    init {
        if (contents.isBlank()) {
            throw IllegalArgumentException("htmlBody is blank")
        }
    }
}
