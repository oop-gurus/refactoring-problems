package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.TimeUnit

class SendAfter(
    private val amount: Long,
    private val unit: TimeUnit,
) {
    fun amount(): Long = amount

    fun unit(): TimeUnit = unit
}
