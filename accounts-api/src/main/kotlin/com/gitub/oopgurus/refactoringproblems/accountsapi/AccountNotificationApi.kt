package com.gitub.oopgurus.refactoringproblems.accountsapi

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(value = "account-notification-api", url = "\${app.feign-client.account.notification.api.url}")
interface AccountNotificationApi {
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun notifyChangedToFrozen(@RequestBody request: AccountFrozenChangedRequest)
}
