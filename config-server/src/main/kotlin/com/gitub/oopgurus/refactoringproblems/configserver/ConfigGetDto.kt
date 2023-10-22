package com.gitub.oopgurus.refactoringproblems.configserver

data class ConfigGetDto(
    val id: Long,
    val isValidSystem: Boolean?, // true이면 system 필드는 non-null이어야 함
    val system: SystemDto?,
    val persons: List<PersonDtoTypeA>?,

    // 모든 key는 대문자로만 이루어져야 함
    // 모든 key는 PROPS_ 으로 시작해야함
    val properties: Map<String, String>?,

    // PROPS_DESCRIPTION_{아무_숫자} 으로 시작하는 properties의 값이 여기에 들어가야함
    val descriptions: List<String>?,
)

