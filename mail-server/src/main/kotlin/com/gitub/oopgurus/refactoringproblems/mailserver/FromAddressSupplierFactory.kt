package com.gitub.oopgurus.refactoringproblems.mailserver

class FromAddressSupplierFactory(
    private val fromAddress: String,
) {
    fun create(): () -> String {
        Regex(".+@.*\\..+").matches(fromAddress).let {
            if (it.not()) {
                throw RuntimeException("이메일 형식 오류")
            }
        }

        return { fromAddress }
    }
}