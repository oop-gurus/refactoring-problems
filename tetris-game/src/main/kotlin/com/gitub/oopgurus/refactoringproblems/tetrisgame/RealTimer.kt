package com.gitub.oopgurus.refactoringproblems.tetrisgame

import javax.swing.Timer

class RealTimer(
    private val timer: Timer
) : GameTimer {
    override fun start() {
        timer.start()
    }

    override fun stop() {
        timer.stop()
    }
}