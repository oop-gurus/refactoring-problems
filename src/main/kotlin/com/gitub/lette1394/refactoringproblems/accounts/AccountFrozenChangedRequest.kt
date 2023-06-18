package com.gitub.lette1394.refactoringproblems.accounts

data class AccountFrozenChangedRequest(
    val accountId: Long,
    val isFrozen: Boolean
)
