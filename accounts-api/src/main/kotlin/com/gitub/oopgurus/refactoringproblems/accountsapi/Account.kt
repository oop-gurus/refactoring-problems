package com.gitub.oopgurus.refactoringproblems.accountsapi

import mu.KotlinLogging
import java.math.BigDecimal

class Account(
    private val accountEntity: AccountEntity,
    private var frozenAction: FrozenAction,
    private var unfrozenAction: UnfrozenAction,
) {
    private val log = KotlinLogging.logger {}

    fun verified() {
        TODO()
    }

    fun close() {
        TODO()
    }

    fun freeze() {
        if (!accountEntity.isVerified) {
            log.info { "확인되지 않은 계좌는 동결할 수 없습니다. 요청을 무시합니다." }
            return
        }
        if (accountEntity.isClosed) {
            log.info { "계좌가 이미 닫혀있습니다. 동결할 수 없습니다. 요청을 무시합니다." }
            return
        }

        frozenAction = frozenAction.invoke(accountEntity)
    }

    fun deposit(amount: BigDecimal) {
        if (accountEntity.isClosed) {
            log.info { "계좌가 이미 닫혀있습니다. 입금할 수 없습니다. 요청을 무시합니다." }
            return
        }

        unfrozenAction = unfrozenAction.invoke(accountEntity)
        accountEntity.balance = accountEntity.balance.add(amount)
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

        unfrozenAction = unfrozenAction.invoke(accountEntity)
        val subtracted = accountEntity.balance.subtract(amount)
        if (subtracted < BigDecimal.ZERO) {
            log.info { "잔액이 부족합니다. 출금할 수 없습니다." }
            throw IllegalArgumentException("잔액이 부족합니다. 출금할 수 없습니다.")
        }
        accountEntity.balance = subtracted
    }
}


interface FrozenAction {
    fun invoke(accountEntity: AccountEntity): FrozenAction
}

class DoNothing2 : FrozenAction {
    override fun invoke(accountEntity: AccountEntity): FrozenAction {
        // do nothing
        return this
    }
}

class NotifyFreeze(
    private val accountNotificationApi: AccountNotificationApi,
) : FrozenAction {
    override fun invoke(accountEntity: AccountEntity): FrozenAction {
        accountEntity.isFrozen = true
        accountNotificationApi.notifyChangedToFrozen(
            AccountFrozenChangedRequest(
                accountId = accountEntity.id!!,
                isFrozen = true,
            ),
        )
        return DoNothing2()
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

class NotifyUnfreeze(
    private val accountNotificationApi: AccountNotificationApi,
) : UnfrozenAction {
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
