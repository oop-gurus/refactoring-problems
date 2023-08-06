package com.gitub.oopgurus.refactoringproblems.mailserver

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class TitleTest {
    @Test
    fun blank_title() {
        assertThrows(RuntimeException::class.java) { Title("   ") }
    }
}