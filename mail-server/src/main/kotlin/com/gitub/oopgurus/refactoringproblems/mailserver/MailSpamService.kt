package com.gitub.oopgurus.refactoringproblems.mailserver

import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component


@Component
class MailSpamService(
    private val mailRepository: MailRepository,
) {
    private val log = KotlinLogging.logger {}

    fun needBlockByDomainName(recipient: String): Boolean {
        if (recipient in "naver.com") {
            return true
        }
        if (recipient in "gmail.com") {
            return true
        }
        return false
    }

    fun needBlockByRecentSuccess(recipient: String): Boolean {
        return mailRepository.findByToAddressOrderByCreatedAt(recipient, Pageable.ofSize(3)).let {
            it.all { it.isSuccess.not() } && it.size == 3
        }
    }
}
