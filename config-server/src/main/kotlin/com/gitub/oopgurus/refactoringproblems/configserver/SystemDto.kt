package com.gitub.oopgurus.refactoringproblems.configserver

data class SystemDto(
    val id: Long,
    val on: Boolean?, // on=true이면 off=false이어야 함
    val off: Boolean?, // on=false이면 on=true이어야 함
    val notes: String?, // on=true이면 notes가 non-null 이어야 함
)
