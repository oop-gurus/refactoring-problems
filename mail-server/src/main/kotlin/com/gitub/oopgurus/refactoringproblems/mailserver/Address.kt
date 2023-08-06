package com.gitub.oopgurus.refactoringproblems.mailserver

data class Address(
        val value: String,
) {
    init {
        REGEX.matches(value).let {
            if (it.not()) {
                throw RuntimeException("이메일 형식 오류")
            }
        }
    }

    companion object {
        val REGEX = Regex(".+@.*\\..+")
    }
}
