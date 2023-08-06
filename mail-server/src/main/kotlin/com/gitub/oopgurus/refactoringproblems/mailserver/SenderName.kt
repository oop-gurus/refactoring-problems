package com.gitub.oopgurus.refactoringproblems.mailserver

data class SenderName(val value: String) {
    init {
        if (value.isBlank()) {
            throw RuntimeException("발신자 이름이 비어있습니다")
        }
    }
}