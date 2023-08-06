package com.gitub.oopgurus.refactoringproblems.mailserver

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class HtmlBodyTest {
    @Test
    fun blank_content() {
        assertThrows(RuntimeException::class.java) { HtmlBody("   ") }
    }
}