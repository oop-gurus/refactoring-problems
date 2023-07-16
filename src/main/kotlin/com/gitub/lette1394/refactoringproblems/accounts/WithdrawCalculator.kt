package com.gitub.lette1394.refactoringproblems.accounts

import mu.KotlinLogging
import java.math.BigDecimal

object WithdrawCalculator {
    private val log = KotlinLogging.logger {}
    fun calculate(accountBalance: BigDecimal, amount: BigDecimal): BigDecimal {
        val subtracted = accountBalance.subtract(amount)
        if (subtracted < BigDecimal.ZERO) {
            log.info { "잔액이 부족합니다. 출금할 수 없습니다." }
            throw IllegalArgumentException("잔액이 부족합니다. 출금할 수 없습니다.")
        }
        return subtracted
    }
}