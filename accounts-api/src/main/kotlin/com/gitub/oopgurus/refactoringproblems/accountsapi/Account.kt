package com.gitub.oopgurus.refactoringproblems.accountsapi

import mu.KotlinLogging
import java.math.BigDecimal

class Account(
    private val accountEntity: AccountEntity,
    private val accountNotificationApi: AccountNotificationApi,

) {
    private val log = KotlinLogging.logger {}

    private var unFrozenAction: UnfrozenAction = object : UnfrozenAction {
        override fun invoke(accountEntity: AccountEntity): UnfrozenAction {
            accountEntity.isFrozen = false

            accountNotificationApi.notifyChangedToFrozen(
                AccountFrozenChangedRequest(
                    accountId = accountEntity.id!!,
                    isFrozen = false,
                )
            )

            return DoNothing()
        }
    }

    fun verified() {
        TODO()
    }

    fun close() {
        TODO()
    }

    fun freeze() {
        TODO()
    }

    fun deposit(amount: BigDecimal) {
        TODO()
    }

    fun withdraw(amount: BigDecimal) {
        if (!accountEntity.isVerified) {
            log.info { "확인되지 않은 계좌는 출금할 수 없습니다. 요청을 무시합니다." }
            return
        }
        if (accountEntity.isClosed) {
            log.info { "계좌가 이미 닫혀있습니다. 출금할 수 없습니다. 요청을 무시합니다." }
            return
        }

        unFrozenAction = unFrozenAction.invoke(accountEntity)
        val subtracted = accountEntity.balance.subtract(amount)
        if (subtracted < BigDecimal.ZERO) {
            log.info { "잔액이 부족합니다. 출금할 수 없습니다." }
            throw IllegalArgumentException("잔액이 부족합니다. 출금할 수 없습니다.")
        }
        accountEntity.balance = subtracted
    }
}


interface UnfrozenAction {
    fun invoke(accountEntity: AccountEntity): UnfrozenAction
}

class DoNothing : UnfrozenAction {
    override fun invoke(accountEntity: AccountEntity): UnfrozenAction {
        // do nothing
        return this
    }
}
