package com.gitub.oopgurus.refactoringproblems.tetrisgame

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BoardTest : StringSpec({

    "상태가 서로 같다" {
        Board(Tetris()) shouldBe Board(Tetris())
    }
})
