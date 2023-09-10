package com.gitub.oopgurus.refactoringproblems.configserver

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<PersonEntity, Long> {
    fun findAllByConfigId(configId: Long): List<PersonEntity>
}
