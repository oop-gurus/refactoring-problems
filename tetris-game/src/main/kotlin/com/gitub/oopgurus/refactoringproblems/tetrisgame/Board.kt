package com.gitub.oopgurus.refactoringproblems.tetrisgame

import java.awt.Color
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.Timer


class Board(parent: Tetris) : JPanel() {
    private val BOARD_WIDTH = 10
    private val BOARD_HEIGHT = 22
    private val PERIOD_INTERVAL = 300
    private var timer: Timer? = null
    private var isFallingFinished = false
    private var isPaused = false
    private var numLinesRemoved = 0
    private var curX = 0
    private var curY = 0
    private var statusbar: JLabel? = null
    private var curPiece: Shape = Shape()
    private lateinit var board: Array<Tetrominoe?>

    init {
        initBoard(parent)
    }

    private fun initBoard(parent: Tetris) {
        setFocusable(true)
        statusbar = parent.statusBar
        addKeyListener(TAdapter())
    }

    private fun squareWidth(): Int {
        return size.getWidth().toInt() / BOARD_WIDTH
    }

    private fun squareHeight(): Int {
        return size.getHeight().toInt() / BOARD_HEIGHT
    }

    private fun shapeAt(x: Int, y: Int): Tetrominoe? {
        return board[y * BOARD_WIDTH + x]
    }

    fun start() {
        curPiece = Shape()
        board = arrayOfNulls<Tetrominoe>(BOARD_WIDTH * BOARD_HEIGHT)
        clearBoard()
        newPiece()
        timer = Timer(PERIOD_INTERVAL, GameCycle())
        timer!!.start()
    }

    private fun pause() {
        isPaused = !isPaused
        if (isPaused) {
            statusbar!!.setText("paused")
        } else {
            statusbar!!.setText(numLinesRemoved.toString())
        }
        repaint()
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        doDrawing(g)
    }

    private fun doDrawing(g: Graphics) {
        val size = size
        val boardTop = size.getHeight().toInt() - BOARD_HEIGHT * squareHeight()
        for (i in 0 until BOARD_HEIGHT) {
            for (j in 0 until BOARD_WIDTH) {
                val shape: Tetrominoe = shapeAt(j, BOARD_HEIGHT - i - 1)!!
                if (shape !== Tetrominoe.NoShape) {
                    drawSquare(
                        g, j * squareWidth(),
                        boardTop + i * squareHeight(), shape
                    )
                }
            }
        }
        if (curPiece.shape !== Tetrominoe.NoShape) {
            for (i in 0..3) {
                val x: Int = curX + curPiece.x(i)
                val y: Int = curY - curPiece.y(i)
                drawSquare(
                    g, x * squareWidth(),
                    boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                    curPiece.shape
                )
            }
        }
    }

    private fun dropDown() {
        var newY = curY
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break
            }
            newY--
        }
        pieceDropped()
    }

    private fun oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped()
        }
    }

    private fun clearBoard() {
        for (i in 0 until BOARD_HEIGHT * BOARD_WIDTH) {
            board[i] = Tetrominoe.NoShape
        }
    }

    private fun pieceDropped() {
        for (i in 0..3) {
            val x: Int = curX + curPiece.x(i)
            val y: Int = curY - curPiece.y(i)
            board[y * BOARD_WIDTH + x] = curPiece.shape
        }
        removeFullLines()
        if (!isFallingFinished) {
            newPiece()
        }
    }

    private fun newPiece() {
        curPiece.setRandomShape()
        curX = BOARD_WIDTH / 2 + 1
        curY = BOARD_HEIGHT - 1 + curPiece.minY()
        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoe.NoShape)
            timer!!.stop()
            val msg = String.format("Game over. Score: %d", numLinesRemoved)
            statusbar!!.setText(msg)
        }
    }

    private fun tryMove(newPiece: Shape, newX: Int, newY: Int): Boolean {
        for (i in 0..3) {
            val x: Int = newX + newPiece.x(i)
            val y: Int = newY - newPiece.y(i)
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false
            }
            if (shapeAt(x, y) !== Tetrominoe.NoShape) {
                return false
            }
        }
        curPiece = newPiece
        curX = newX
        curY = newY
        repaint()
        return true
    }

    private fun removeFullLines() {
        var numFullLines = 0
        for (i in BOARD_HEIGHT - 1 downTo 0) {
            var lineIsFull = true
            for (j in 0 until BOARD_WIDTH) {
                if (shapeAt(j, i) === Tetrominoe.NoShape) {
                    lineIsFull = false
                    break
                }
            }
            if (lineIsFull) {
                numFullLines++
                for (k in i until BOARD_HEIGHT - 1) {
                    for (j in 0 until BOARD_WIDTH) {
                        board[k * BOARD_WIDTH + j] = shapeAt(j, k + 1)
                    }
                }
            }
        }
        if (numFullLines > 0) {
            numLinesRemoved += numFullLines
            statusbar!!.setText(numLinesRemoved.toString())
            isFallingFinished = true
            curPiece.setShape(Tetrominoe.NoShape)
        }
    }

    private fun drawSquare(g: Graphics, x: Int, y: Int, shape: Tetrominoe?) {
        val colors = arrayOf(
            Color(0, 0, 0), Color(204, 102, 102),
            Color(102, 204, 102), Color(102, 102, 204),
            Color(204, 204, 102), Color(204, 102, 204),
            Color(102, 204, 204), Color(218, 170, 0)
        )
        val color = colors[shape!!.ordinal]
        g.color = color
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2)
        g.color = color.brighter()
        g.drawLine(x, y + squareHeight() - 1, x, y)
        g.drawLine(x, y, x + squareWidth() - 1, y)
        g.color = color.darker()
        g.drawLine(
            x + 1, y + squareHeight() - 1,
            x + squareWidth() - 1, y + squareHeight() - 1
        )
        g.drawLine(
            x + squareWidth() - 1, y + squareHeight() - 1,
            x + squareWidth() - 1, y + 1
        )
    }

    private inner class GameCycle : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            doGameCycle()
        }
    }

    private fun doGameCycle() {
        update()
        repaint()
    }

    private fun update() {
        if (isPaused) {
            return
        }
        if (isFallingFinished) {
            isFallingFinished = false
            newPiece()
        } else {
            oneLineDown()
        }
    }

    internal inner class TAdapter : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            if (curPiece.shape === Tetrominoe.NoShape) {
                return
            }
            val keycode = e.keyCode
            when (keycode) {
                KeyEvent.VK_P -> pause()
                KeyEvent.VK_LEFT -> tryMove(curPiece, curX - 1, curY)
                KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY)
                KeyEvent.VK_DOWN -> tryMove(curPiece.rotateRight(), curX, curY)
                KeyEvent.VK_UP -> tryMove(curPiece.rotateLeft(), curX, curY)
                KeyEvent.VK_SPACE -> dropDown()
                KeyEvent.VK_D -> oneLineDown()
            }
        }
    }
}
