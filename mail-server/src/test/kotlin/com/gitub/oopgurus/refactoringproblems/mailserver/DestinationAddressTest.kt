package com.gitub.oopgurus.refactoringproblems.mailserver

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class DestinationAddressTest {
    @Test
    fun block_domain() {
        assertThrows(RuntimeException::class.java) { DestinationAddress("oh980225@naver.com") }
    }
}