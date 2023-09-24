package com.gitub.oopgurus.refactoringproblems.configserver

class ConfigGetDtoBuilder(
    private val system: SystemDto?,
    private val persons: List<PersonDto>,
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
        return ConfigGetDto(
            id = idGet(),
            isValidSystem = system != null,
            system = system,
            persons = persons,
            properties = propertiesGet(),
            descriptions = descriptionsGet(),
        )
    }
}