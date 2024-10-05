package com.gitub.oopgurus.refactoringproblems.tetrisgame

import java.util.*
import kotlin.math.abs

object RandomTetrominoeStream: TetrominoeStream {
    private val random = Random()

    override fun next(): Tetrominoe {
        val x = (abs(random.nextInt().toDouble()) % 7 + 1).toInt()
        val values = Tetrominoe.values()
        return values[x]
    }
}