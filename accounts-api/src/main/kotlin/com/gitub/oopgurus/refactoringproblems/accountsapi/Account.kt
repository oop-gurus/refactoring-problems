package com.gitub.oopgurus.refactoringproblems.accountsapi

import java.math.BigDecimal

data class Account(
    val accountId: Long?,
    val isVerified: Boolean,
    val isClosed: Boolean,
    val isFrozen: Boolean,
    val balance: BigDecimal,
)
