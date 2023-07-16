package com.gitub.oopgurus.refactoringproblems.accountsapi

import java.math.BigDecimal

data class AccountDepositRequest(
    val amount: BigDecimal,
)
