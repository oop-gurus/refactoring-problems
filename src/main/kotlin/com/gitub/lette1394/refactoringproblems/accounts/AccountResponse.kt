package com.gitub.lette1394.refactoringproblems.accounts

import java.math.BigDecimal

data class AccountResponse private constructor(
        val accountId: Long,
        val isVerified: Boolean,
        val isClosed: Boolean,
        val isFrozen: Boolean,
        val balance: BigDecimal,
) {
    companion object {
        fun of(account: Account): AccountResponse {
            return AccountResponse(
                    accountId = account.accountId ?: throw RuntimeException("response have no id"),
                    isVerified = account.isVerified,
                    isClosed = account.isClosed,
                    isFrozen = account.isFrozen,
                    balance = account.balance
            )
        }
    }
}