package com.gitub.lette1394.refactoringproblems.accounts

import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<AccountEntity, Long>
