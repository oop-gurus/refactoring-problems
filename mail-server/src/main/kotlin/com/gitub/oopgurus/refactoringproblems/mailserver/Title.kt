package com.gitub.oopgurus.refactoringproblems.mailserver

data class Title(val value: String) {
    init {
        if (value.isBlank()) {
            throw RuntimeException("제목이 비어있습니다")
        }
    }
}
