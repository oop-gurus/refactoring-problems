package com.gitub.oopgurus.refactoringproblems.mailserver

class FromNameSupplierFactory(
    private val fromName: String,
) {
    fun create(): () -> String {
        if (fromName.isBlank()) {
            throw RuntimeException("발신자 이름이 비어있습니다")
        }

        return { fromName }
    }
}