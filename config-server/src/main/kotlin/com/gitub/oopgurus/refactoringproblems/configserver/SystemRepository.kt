package com.gitub.oopgurus.refactoringproblems.configserver

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SystemRepository : JpaRepository<SystemEntity, Long> {
    fun findByConfigId(configId: Long): SystemEntity?
}
