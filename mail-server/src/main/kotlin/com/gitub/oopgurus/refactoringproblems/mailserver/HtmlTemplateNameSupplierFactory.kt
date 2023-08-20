package com.gitub.oopgurus.refactoringproblems.mailserver

class HtmlTemplateNameSupplierFactory(
    private val htmlTemplateName: String,
) {
    fun create(): () -> String {
        if (htmlTemplateName.isBlank()) {
            throw RuntimeException("템플릿 이름이 비어있습니다")
        }

        return { htmlTemplateName }
    }
}