package com.gitub.oopgurus.refactoringproblems.accountsapi

data class AccountFrozenChangedRequest(
    val accountId: Long,
    val isFrozen: Boolean
)
