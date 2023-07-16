package com.gitub.lette1394.refactoringproblems.accounts

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
    fun createAccount(): Account {
        val accountEntity = accountRepository.save(
                AccountEntity(
                        isVerified = false,
                        isClosed = false,
                        isFrozen = false,
                        balance = BigDecimal.ZERO,
                )
        )

        return AccountMapper.toAccount(accountEntity)
    }

    @Transactional
    fun getAccount(accountId: Long): Account {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }

        return AccountMapper.toAccount(accountEntity)
    }

    @Transactional
    fun holderVerified(accountId: Long) {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }
        val account = AccountMapper.toAccount(accountEntity)

        account.verify { accountEntity.isVerified = true }
    }

    @Transactional
    fun closeAccount(accountId: Long) {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }
        val account = AccountMapper.toAccount(accountEntity)

        account.verify { accountEntity.isClosed = true }
    }

    @Transactional
    fun freezeAccount(accountId: Long) {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }
        val account = AccountMapper.toAccount(accountEntity)

        account.freeze {
            accountEntity.isFrozen = true
            accountNotificationApi.notifyChangedToFrozen(
                    AccountFrozenChangedRequest(
                            accountId = accountId,
                            isFrozen = true,
                    ),
            )
        }
    }

    @Transactional
    fun deposit(accountId: Long, amount: BigDecimal) {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }
        val account = AccountMapper.toAccount(accountEntity)

        account.deposit { accountEntity.balance = accountEntity.balance.add(amount) }

        account.melt {
            accountEntity.isFrozen = false
            accountNotificationApi.notifyChangedToFrozen(
                    AccountFrozenChangedRequest(
                            accountId = accountId,
                            isFrozen = false,
                    ),
            )
        }
    }

    @Transactional
    fun withdraw(accountId: Long, amount: BigDecimal) {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }
        val account = AccountMapper.toAccount(accountEntity)

        account.withdraw { accountEntity.balance = WithdrawCalculator.calculate(accountEntity.balance, amount) }

        account.melt {
            accountEntity.isFrozen = false
            accountNotificationApi.notifyChangedToFrozen(
                    AccountFrozenChangedRequest(
                            accountId = accountId,
                            isFrozen = false,
                    ),
            )
        }
    }
}
