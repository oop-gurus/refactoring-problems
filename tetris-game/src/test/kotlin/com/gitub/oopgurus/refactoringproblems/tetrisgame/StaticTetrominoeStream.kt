package com.gitub.oopgurus.refactoringproblems.tetrisgame

class StaticTetrominoeStream(
    private val tetrominoe: Tetrominoe
): TetrominoeStream {
    override fun next(): Tetrominoe {
        return tetrominoe
    }
}