package com.gitub.lette1394.refactoringproblems.accounts

import java.math.BigDecimal

data class Account(
    val accountId: Long?,
    val isVerified: Boolean,
    val isClosed: Boolean,
    val isFrozen: Boolean,
    val balance: BigDecimal,
)
