package com.gitub.oopgurus.refactoringproblems.tetrisgame

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BoardTest : StringSpec({

    "상태가 서로 같다" {
        Board(Tetris(), NoGameCycle) shouldBe Board(Tetris(), NoGameCycle)
    }

    "최초 상태" {
        // 시간이 흐르지 않았고
        // ??? timer = NullTimer?

        // 유저 입력이 없는 경우
        // ??? user = NullUser?

        // 최초 상태를 검사한다
        val board = Board(Tetris(), NoGameCycle)
        board.isPaused shouldBe false
        board.numLinesRemoved shouldBe 0
        board.curX shouldBe null
        board.curY shouldBe null
        board.curPiece shouldBe null
        board.board shouldBe Array(board.boardWidth * board.boardHeight) { Tetrominoe.NoShape }
    }
})
