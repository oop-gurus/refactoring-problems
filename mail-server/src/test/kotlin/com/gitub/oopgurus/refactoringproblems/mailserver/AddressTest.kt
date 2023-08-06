package com.gitub.oopgurus.refactoringproblems.mailserver

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class AddressTest {
    @Test
    fun validate_regex() {
        assertThrows(RuntimeException::class.java) { Address("oh980225naver.com") }
    }
}