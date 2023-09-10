package com.gitub.oopgurus.refactoringproblems.configserver

data class PersonDto(
    val id: Long,

    // true이면 isForeigner=false이어야 함
    val isKorean: Boolean?,

    // true이면 name은 firstName + lastName 이어야 함.
    // false이면 name은 lastName + firstName 이어야함
    val isForeigner: Boolean?,

    val name: String,
    val firstName: String?,
    val lastName: String?,

    // 이메일 형식이어야 함
    val email: String?,

    // true이면 phone field 형식은 010-xxxx-yyyy
    val isMobilePhone: Boolean?,
    // true이면 phone field 형식은 02-xxxx-yyyy
    val isOfficePhone: Boolean?,
    val phone: String?, //
)
