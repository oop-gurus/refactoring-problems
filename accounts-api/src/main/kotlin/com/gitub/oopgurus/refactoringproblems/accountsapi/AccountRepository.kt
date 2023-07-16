package com.gitub.oopgurus.refactoringproblems.accountsapi

import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<AccountEntity, Long>
