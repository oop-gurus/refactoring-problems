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

        // 결국 if else 가 생기는데...
        // enum으로 묶고 싶어도 그걸 분기할 때, if else 가 생기고..
        // 이걸 when 으로 풀려해도 어딘가에는 if else 가 생길듯
        // 그래도 로직이 if else 된게 아닌, 상태를 정의하는게 if else 라 복잡성이라 보기는 좀 그런가..?
        // 그 차이를 잘 모르겠네...
        // 그냥 어떻게 적용해서 써야할지 전혀 모르겠다... 심플한 것부터 하면 감 좀 잡을 수 있을 거 같은데..
        // 공존 가능한 상태도 있고, 각 flow 가 다다르니 감이 하나도 안잡히네..
        val accountState = if(!accountEntity.isVerified) {
            InVerified()
        } else if(accountEntity.isClosed) {
            Closed()
        } else {
            Verified()
        }

        accountState.freeze {
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

        val accountState = if(accountEntity.isClosed) {
            Closed()
        } else if(!accountEntity.isVerified) {
            InVerified()
        } else {
            Verified()
        }

        accountState.deposit {
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

        val accountState = if(!accountEntity.isVerified) {
            InVerified()
        } else if(accountEntity.isClosed) {
            Closed()
        } else {
            Verified()
        }

        accountState.withdraw {
            val subtracted = accountEntity.balance.subtract(amount)
            if (subtracted < BigDecimal.ZERO) {
                log.info { "잔액이 부족합니다. 출금할 수 없습니다." }
                throw IllegalArgumentException("잔액이 부족합니다. 출금할 수 없습니다.")
            }
            accountEntity.balance = subtracted

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
}
