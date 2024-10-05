package com.gitub.oopgurus.refactoringproblems.tetrisgame

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class GameCycle : ActionListener {
    var board: Board? = null

    override fun actionPerformed(e: ActionEvent?) {
        board!!.doGameCycle()
    }
}