package com.gitub.oopgurus.refactoringproblems.tetrisgame

import java.awt.event.ActionListener

class StaticTimer(
    private val ticks: Int,
    private val actionListener: ActionListener
) : GameTimer {
    override fun start() {
        repeat(ticks) {
            actionListener.actionPerformed(null)
        }
    }

    override fun stop() {
        repeat(ticks) {
            actionListener.actionPerformed(null)
        }
    }
}