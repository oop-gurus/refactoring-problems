package com.gitub.oopgurus.refactoringproblems.mailserver

data class HtmlTemplateName(val value: String) {
    init {
        if (value.isBlank()) {
            throw RuntimeException("템플릿 이름이 비어있습니다")
        }
    }
}
