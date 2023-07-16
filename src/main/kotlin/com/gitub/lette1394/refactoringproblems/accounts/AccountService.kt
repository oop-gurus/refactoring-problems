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

        return Account(
                accountId = accountEntity.id,
                isVerified = accountEntity.isVerified,
                isClosed = accountEntity.isClosed,
                isFrozen = accountEntity.isFrozen,
                balance = accountEntity.balance,
        )
    }

    @Transactional
    fun getAccount(accountId: Long): Account {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }

        return Account(
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

        getAccountState(accountEntity).verify { accountEntity.isVerified = true }
    }

    @Transactional
    fun closeAccount(accountId: Long) {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }

        getAccountState(accountEntity).verify { accountEntity.isClosed = true }
    }

    @Transactional
    fun freezeAccount(accountId: Long) {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }

        getAccountState(accountEntity).freeze {
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

        getAccountState(accountEntity).deposit {
            accountEntity.balance = accountEntity.balance.add(amount)

            if (accountEntity.isFrozen) {
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

    @Transactional
    fun withdraw(accountId: Long, amount: BigDecimal) {
        val accountEntity = accountRepository.findById(accountId)
                .orElseThrow { throw RuntimeException("Account not found") }

        getAccountState(accountEntity).withdraw {
            accountEntity.balance = WithdrawCalculator.calculate(accountEntity.balance, amount)

            if (accountEntity.isFrozen) {
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

    private fun getAccountState(accountEntity: AccountEntity) =
            if (!accountEntity.isVerified) {
                if (accountEntity.isClosed) {
                    InVerifiedAndClosed()
                }
                InVerified()
            } else {
                if (accountEntity.isClosed) {
                    Closed()
                }
                Verified()
            }
}
