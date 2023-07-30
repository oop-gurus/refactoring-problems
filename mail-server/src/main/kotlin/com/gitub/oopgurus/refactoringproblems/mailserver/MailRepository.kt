package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MailRepository : JpaRepository<MailEntity, Long> {
    fun findByToAddressOrderByCreatedAt(recipient: String, pageable: Pageable): List<MailEntity>

    fun findByToAddressEndingWithOrderByCreatedAt(domain: String, pageable: Pageable): List<MailEntity>
}
