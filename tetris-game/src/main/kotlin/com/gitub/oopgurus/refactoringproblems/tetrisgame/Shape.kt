package com.gitub.oopgurus.refactoringproblems.tetrisgame

import java.util.Random
import kotlin.math.abs
import kotlin.math.min


enum class Tetrominoe {
    NoShape,
    ZShape,
    SShape,
    LineShape,
    TShape,
    SquareShape,
    LShape,
    MirroredLShape
}

class Shape {

    var shape: Tetrominoe? = null
        private set
    private lateinit var coords: Array<IntArray>
    private lateinit var coordsTable: Array<Array<IntArray>>

    init {
        initShape()
    }

    private fun initShape() {
        coords = Array(4) { IntArray(2) }
        coordsTable = arrayOf(
            arrayOf(intArrayOf(0, 0), intArrayOf(0, 0), intArrayOf(0, 0), intArrayOf(0, 0)),
            arrayOf(
                intArrayOf(0, -1), intArrayOf(0, 0), intArrayOf(-1, 0), intArrayOf(-1, 1)
            ),
            arrayOf(intArrayOf(0, -1), intArrayOf(0, 0), intArrayOf(1, 0), intArrayOf(1, 1)),
            arrayOf(intArrayOf(0, -1), intArrayOf(0, 0), intArrayOf(0, 1), intArrayOf(0, 2)),
            arrayOf(
                intArrayOf(-1, 0), intArrayOf(0, 0), intArrayOf(1, 0), intArrayOf(0, 1)
            ),
            arrayOf(intArrayOf(0, 0), intArrayOf(1, 0), intArrayOf(0, 1), intArrayOf(1, 1)),
            arrayOf(intArrayOf(-1, -1), intArrayOf(0, -1), intArrayOf(0, 0), intArrayOf(0, 1)),
            arrayOf(
                intArrayOf(1, -1), intArrayOf(0, -1), intArrayOf(0, 0), intArrayOf(0, 1)
            )
        )
        setShape(Tetrominoe.NoShape)
    }

    fun setShape(shape: Tetrominoe) {
        for (i in 0..3) {
            for (j in 0..1) {
                coords[i][j] = coordsTable[shape.ordinal][i][j]
            }
        }
        this.shape = shape
    }

    private fun setX(index: Int, x: Int) {
        coords[index][0] = x
    }

    private fun setY(index: Int, y: Int) {
        coords[index][1] = y
    }

    fun x(index: Int): Int {
        return coords[index][0]
    }

    fun y(index: Int): Int {
        return coords[index][1]
    }

    fun setRandomShape() {
        val r = Random()
        val x = (abs(r.nextInt().toDouble()) % 7 + 1).toInt()
        val values = Tetrominoe.values()
        setShape(values[x])
    }

    fun minX(): Int {
        var m = coords[0][0]
        for (i in 0..3) {
            m = min(m.toDouble(), coords[i][0].toDouble()).toInt()
        }
        return m
    }

    fun minY(): Int {
        var m = coords[0][1]
        for (i in 0..3) {
            m = min(m.toDouble(), coords[i][1].toDouble()).toInt()
        }
        return m
    }

    fun rotateLeft(): Shape {
        if (shape == Tetrominoe.SquareShape) {
            return this
        }
        val result = Shape()
        result.shape = shape
        for (i in 0..3) {
            result.setX(i, y(i))
            result.setY(i, -x(i))
        }
        return result
    }

    fun rotateRight(): Shape {
        if (shape == Tetrominoe.SquareShape) {
            return this
        }
        val result = Shape()
        result.shape = shape
        for (i in 0..3) {
            result.setX(i, -y(i))
            result.setY(i, x(i))
        }
        return result
    }
}
