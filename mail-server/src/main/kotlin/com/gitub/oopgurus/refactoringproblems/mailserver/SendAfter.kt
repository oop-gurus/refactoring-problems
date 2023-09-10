package com.gitub.oopgurus.refactoringproblems.mailserver

import java.util.concurrent.TimeUnit

data class SendAfter(
    val amount: Long,
    val unit: TimeUnit,
)