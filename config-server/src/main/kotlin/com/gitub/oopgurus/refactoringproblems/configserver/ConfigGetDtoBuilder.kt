package com.gitub.oopgurus.refactoringproblems.configserver

class ConfigGetDtoBuilder: WhatIWantToConfig {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var systemGet: () -> SystemDto? = { throw IllegalStateException() }
    private var personsGet: () -> List<PersonDto> = { throw IllegalStateException() }
    private var propertiesGet: () -> Map<String, String> = { throw IllegalStateException() }
    private var descriptionsGet: () -> List<String> = { throw IllegalStateException() }

    override fun id(id: Long) {
        idGet = { id }
    }

    fun system(system: SystemDto?) {
        systemGet = { system }
    }

    fun persons(persons: List<PersonDto>) {
        personsGet = { persons }
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
            isValidSystem = systemGet() != null,
            system = systemGet(),
            persons = personsGet(),
            properties = propertiesGet(),
            descriptions = descriptionsGet(),
        )
    }
}