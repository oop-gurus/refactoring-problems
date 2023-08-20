package com.gitub.oopgurus.refactoringproblems.mailserver

class TitleSupplierFactory(
    private val title: String,
) {
    fun create(): () -> String {
        if (title.isBlank()) {
            throw RuntimeException("제목이 비어있습니다")
        }

        return { title }
    }
}