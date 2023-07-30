package com.gitub.oopgurus.refactoringproblems.tetrisgame

import java.awt.BorderLayout
import java.awt.EventQueue
import javax.swing.JFrame
import javax.swing.JLabel


/*
Java Tetris game clone

Author: Jan Bodnar
Website: https://zetcode.com
*/
class Tetris : JFrame() {
    var statusBar: JLabel? = null
        private set

    init {
        initUI()
    }

    private fun initUI() {
        statusBar = JLabel(" 0")
        add(statusBar, BorderLayout.SOUTH)
        val board = Board(this)
        add(board)
        board.start()
        setTitle("Tetris")
        setSize(200, 400)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        setLocationRelativeTo(null)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater {
                val game = Tetris()
                game.isVisible = true
            }
        }
    }
}
