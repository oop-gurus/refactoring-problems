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

        // 결국 어떤 상태(accountState)인지 알려주려면 if else 가 생기는데...
        // enum 으로 묶고 싶어도 그걸 분기할 때, if else 가 생길 것이고..
        // 이걸 when 으로 풀려해도 어딘가에는 if else 가 생길듯
        // 그래도 로직이 if else 된게 아닌, 상태를 정의하는게 if else 라 복잡성이라 보기는 좀 그런가..?
        // 그 차이를 잘 모르겠네...
        // 그냥 어떻게 적용해서 써야할지 잘 모르겠다... 심플한 것부터 하면 감 좀 잡을 수 있을 거 같은데..
        // 공존 가능한 상태도 있고, 각 flow 의 세부 조건이 다르니 어떻게 묶어줘야할지 감이 안잡히네..
        // 아님 아까 한대로 가능한 케이스를 모두 상태 객체로 만들어줘야하나?
        // 그러면 또 그만큼 if else 를 써서 어떤 상태인지 정해줘야할텐데... 그게 맞나?
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

        // 이렇게 어떤 로직을 수행할지 통으로 넘겨주는게 맞나..?
        // 어차피 when 만 accountState 에게 위임하는거니 맞나..?
        // 아님 accountState 가 Account 를 알게 하고, 그 안에서 분기처리해줘야하나?
        // 달라지는게 있나?
        // 걍 state 를 사용하는 것 자체를 내가 잘못 이해한건가?
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
