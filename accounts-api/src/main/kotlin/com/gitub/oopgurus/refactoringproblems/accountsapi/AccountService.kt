package com.gitub.oopgurus.refactoringproblems.accountsapi

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountNotificationApi: AccountNotificationApi,
) {
    private val log = KotlinLogging.logger {}

    @Transactional
    fun createAccount(): AccountData {
        val accountEntity = accountRepository.save(
            AccountEntity(
                isVerified = false,
                isClosed = false,
                isFrozen = false,
                balance = BigDecimal.ZERO,
            )
        )

        return AccountData(
            accountId = accountEntity.id,
            isVerified = accountEntity.isVerified,
            isClosed = accountEntity.isClosed,
            isFrozen = accountEntity.isFrozen,
            balance = accountEntity.balance,
        )
    }

    @Transactional
    fun getAccount(accountId: Long): AccountData {
        val accountEntity = accountRepository.findById(accountId)
            .orElseThrow { throw RuntimeException("Account not found") }

        return AccountData(
            accountId = accountId,
            isVerified = accountEntity.isVerified,
            isClosed = accountEntity.isClosed,
            isFrozen = accountEntity.isFrozen,
            balance = accountEntity.balance,
        )
    }

    @Transactional
    fun holderVerified(accountId: Long) {
        val accountEntity = accountRepository.findById(accountId)
            .orElseThrow { throw RuntimeException("Account not found") }

        accountEntity.isVerified = true
    }

    @Transactional
    fun closeAccount(accountId: Long) {
        val accountEntity = accountRepository.findById(accountId)
            .orElseThrow { throw RuntimeException("Account not found") }

        accountEntity.isClosed = true
    }

    @Transactional
    fun freezeAccount(accountId: Long) {
        val accountEntity = accountRepository.findById(accountId)
            .orElseThrow { throw RuntimeException("Account not found") }

        val unfrozenAction = when (accountEntity.isFrozen) {
            true -> NotifyUnfreeze(accountNotificationApi)
            false -> DoNothing()
        }

        val account = Account(
            accountEntity = accountEntity,
            unfrozenAction = unfrozenAction,
            accountNotificationApi = accountNotificationApi,
        )

        account.freeze()
    }

    @Transactional
    fun deposit(accountId: Long, amount: BigDecimal) {
        val accountEntity = accountRepository.findById(accountId)
            .orElseThrow { throw RuntimeException("Account not found") }

        val unfrozenAction = when (accountEntity.isFrozen) {
            true -> NotifyUnfreeze(accountNotificationApi)
            false -> DoNothing()
        }

        val account = Account(
            accountEntity = accountEntity,
            unfrozenAction = unfrozenAction,
            accountNotificationApi = accountNotificationApi,
        )
        account.deposit(amount)
    }


    @Transactional
    fun withdraw(accountId: Long, amount: BigDecimal) {
        val accountEntity = accountRepository.findById(accountId)
            .orElseThrow { throw RuntimeException("Account not found") }

        val unfrozenAction = when (accountEntity.isFrozen) {
            true -> NotifyUnfreeze(accountNotificationApi)
            false -> DoNothing()
        }

        val account = Account(
            accountEntity = accountEntity,
            unfrozenAction = unfrozenAction,
            accountNotificationApi = accountNotificationApi,
        )
        account.withdraw(amount)
    }
}
