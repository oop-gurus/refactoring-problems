package com.gitub.oopgurus.refactoringproblems.configserver

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigRepository : JpaRepository<ConfigEntity, Long>
