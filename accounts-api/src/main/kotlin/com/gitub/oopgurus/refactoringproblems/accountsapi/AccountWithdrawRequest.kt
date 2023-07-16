package com.gitub.oopgurus.refactoringproblems.accountsapi

import java.math.BigDecimal

data class AccountWithdrawRequest(
    val amount: BigDecimal,
)
