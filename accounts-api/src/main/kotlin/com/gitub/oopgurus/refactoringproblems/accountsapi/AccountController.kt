package com.gitub.oopgurus.refactoringproblems.accountsapi

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class AccountController(
    val accountService: AccountService,
) {
    @PostMapping("/v1/accounts")
    fun createAccount(): ResponseEntity<Account> {
        val account = accountService.createAccount()
        return ResponseEntity.ok(account)
    }

    @GetMapping("/v1/accounts/{accountId}")
    fun getAccount(
        @PathVariable accountId: Long,
    ): ResponseEntity<Account> {
        val account = accountService.getAccount(accountId)
        return ResponseEntity.ok(account)
    }

    @PutMapping("/v1/accounts/{accountId}/holder-verified")
    fun holderVerified(
        @PathVariable accountId: Long,
    ): ResponseEntity<Void> {
        accountService.holderVerified(accountId)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/accounts/{accountId}/closed")
    fun closeAccount(
        @PathVariable accountId: Long,
    ): ResponseEntity<Void> {
        accountService.closeAccount(accountId)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/v1/accounts/{accountId}/frozen")
    fun freezeAccount(
        @PathVariable accountId: Long,
    ): ResponseEntity<Void> {
        accountService.freezeAccount(accountId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/v1/accounts/{accountId}/deposit")
    fun deposit(
        @PathVariable accountId: Long,
        @RequestBody requestBody: AccountDepositRequest,
    ): ResponseEntity<Void> {
        accountService.deposit(
            accountId = accountId,
            amount = requestBody.amount
        )
        return ResponseEntity.ok().build()
    }


    @PostMapping("/v1/accounts/{accountId}/withdraw")
    fun withdraw(
        @PathVariable accountId: Long,
        @RequestBody requestBody: AccountWithdrawRequest,
    ): ResponseEntity<Void> {
        accountService.withdraw(
            accountId = accountId,
            amount = requestBody.amount
        )
        return ResponseEntity.ok().build()
    }
}
