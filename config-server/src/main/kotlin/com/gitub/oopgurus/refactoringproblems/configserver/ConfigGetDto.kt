package com.gitub.oopgurus.refactoringproblems.configserver

data class ConfigGetDto(
    val id: Long,
    val isValidSystem: Boolean?, // true이면 system 필드는 non-null이어야 함
    val system: SystemDto?,
    val persons: List<PersonDtoType>?,

    // 모든 key는 대문자로만 이루어져야 함
    // 모든 key는 PROPS_ 으로 시작해야함
    val properties: Map<String, String>?,

    // PROPS_DESCRIPTION_{아무_숫자} 으로 시작하는 properties의 값이 여기에 들어가야함
    val descriptions: List<String>?,
)

class ConfigGetDtoBuilder() {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var isValidSystemGet: () -> Boolean? = { throw IllegalStateException() }
    private var systemGet: () -> SystemDto? = { throw IllegalStateException() }
    private var personsGet: () -> List<PersonDtoType>? = { throw IllegalStateException() }
    private var propertiesGet: () -> Map<String, String>? = { throw IllegalStateException() }
    private var descriptionsGet: () -> List<String>? = { throw IllegalStateException() }

    fun ID_값_채우기(id: Long): ConfigGetDtoBuilder {
        idGet = { id }
        return this
    }

    fun SYSTEM_값_채우기(systemDto: SystemDto?): ConfigGetDtoBuilder {
        systemGet = { systemDto }
        isValidSystemGet = { systemDto != null }
        return this
    }

    fun PERSONS_값_채우기(persons: List<PersonDtoType>?): ConfigGetDtoBuilder {
        personsGet = { persons }
        return this
    }

    fun PROPERTIES_값_채우기(properties: Properties): ConfigGetDtoBuilder {
        // 여기선 둘다 원해 (값 채워줘)
        val 내가_원하는_것 = WhatIWantBoth()

        // 줘봐 채워줄께
        properties.okay_i_will_give_you_what_you_want(내가_원하는_것)

        propertiesGet = { 내가_원하는_것.getProperties() }
        descriptionsGet = { 내가_원하는_것.getDescriptions() }
        return this
    }

    fun result(): ConfigGetDto {
        return ConfigGetDto(
            id = idGet(),
            isValidSystem = isValidSystemGet(),
            system = systemGet(),
            persons = personsGet(),
            properties = propertiesGet(),
            descriptions = descriptionsGet(),
        )
    }
}