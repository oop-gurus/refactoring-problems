package com.gitub.oopgurus.refactoringproblems.tetrisgame

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BoardTest : StringSpec({

    "상태가 서로 같다" {
        Board(Tetris(), NoTimer, RandomTetrominoeStream) shouldBe Board(Tetris(), NoTimer, RandomTetrominoeStream)
    }

    "최초 상태" {
        // 시간이 흐르지 않았고
        // ??? timer = NullTimer?

        // 유저 입력이 없는 경우
        // ??? user = NullUser?

        // 최초 상태를 검사한다
        val board = Board(Tetris(), NoTimer, RandomTetrominoeStream)
        board.isPaused shouldBe false
        board.numLinesRemoved shouldBe 0
        board.curX shouldBe null
        board.curY shouldBe null
        board.curPiece shouldBe null
        board.board contentDeepEquals Array(board.boardWidth * board.boardHeight) { Tetrominoe.NoShape }
    }

    "시간이 1 tick 흘렀을 때" {
        // 시간이 흐르고
        // ??? timer = Timer?

        // 유저 입력이 없는 경우
        // ??? user = NullUser?

        // 시간이 조금 흐른 상태를 검사한다
        val gameCycle = GameCycle()
        val board = Board(Tetris(), StaticTimer(1, gameCycle), StaticTetrominoeStream(Tetrominoe.LineShape))
        gameCycle.board = board
        board.start()

        assertSoftly {
            board.isPaused shouldBe false
            board.numLinesRemoved shouldBe 0
            board.curX shouldBe 6
            board.curY shouldBe 19
            board.curPiece shouldBe Shape().also { it.setShape(Tetrominoe.LineShape) }
            board.board contentDeepEquals Array(board.boardWidth * board.boardHeight) { Tetrominoe.NoShape }
        }
    }
})
