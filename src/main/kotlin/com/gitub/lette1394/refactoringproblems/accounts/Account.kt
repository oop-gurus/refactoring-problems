package com.gitub.lette1394.refactoringproblems.accounts

import java.math.BigDecimal

class Account(
        val accountId: Long?,
        val isVerified: Boolean,
        val isClosed: Boolean,
        val isFrozen: Boolean,
        val balance: BigDecimal,
        private val state: AccountState,
) {
    fun verify(onVerify: () -> Unit) {
        state.verify(onVerify)
    }

    fun freeze(onFreeze: () -> Unit) {
        state.freeze(onFreeze)
    }

    fun melt(onMelt: () -> Unit) {
        state.melt(onMelt)
    }

    fun close(onClose: () -> Unit) {
        state.close(onClose)
    }

    fun withdraw(onWithdraw: () -> Unit) {
        state.withdraw(onWithdraw)
    }

    fun deposit(onDeposit: () -> Unit) {
        state.deposit(onDeposit)
    }
}
