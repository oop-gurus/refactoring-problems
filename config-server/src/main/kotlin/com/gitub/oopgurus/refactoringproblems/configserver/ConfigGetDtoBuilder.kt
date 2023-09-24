package com.gitub.oopgurus.refactoringproblems.configserver

class ConfigGetDtoBuilder(
    private val system: System?,
    private val persons: List<Person>,
): WhatIWantToConfig {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var propertiesGet: () -> Map<String, String> = { throw IllegalStateException() }
    private var descriptionsGet: () -> List<String> = { throw IllegalStateException() }

    override fun id(id: Long) {
        idGet = { id }
    }

    override fun properties(properties: Map<String, String>) {
        propertiesGet = { properties }
    }

    override fun descriptions(descriptions: List<String>) {
        descriptionsGet = { descriptions }
    }

    fun build(): ConfigGetDto {
        val systemDtoBuilder = SystemDtoBuilder()
        system?.okay_i_will_give_you_what_you_want(systemDtoBuilder)
        val personDtoList = persons.map {
            val personDtoBuilder = PersonDtoBuilder()
            it.okay_i_will_give_you_what_you_want(personDtoBuilder)
            personDtoBuilder.build()
        }

        return ConfigGetDto(
            id = idGet(),
            isValidSystem = system != null,
            system = systemDtoBuilder.build(),
            persons = personDtoList,
            properties = propertiesGet(),
            descriptions = descriptionsGet(),
        )
    }
}