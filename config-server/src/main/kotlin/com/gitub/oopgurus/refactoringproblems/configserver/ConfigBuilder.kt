package com.gitub.oopgurus.refactoringproblems.configserver

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class ConfigBuilder(
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository,
) {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var propertiesGet: () -> Properties = { throw IllegalStateException() }

    fun id(id: Long) {
        idGet = { id }
    }

    fun properties(properties: String) {
        propertiesGet = { Properties.parse(properties) }
    }

    fun build(): Config {
        val persons = personRepository.findAllByConfigId(idGet()).map {
            Person(it)
        }
        val systemEntity = systemRepository.findByConfigId(idGet())
        return Config(
            id = idGet(),
            properties = propertiesGet(),
            persons =  Persons(persons),
            system = systemEntity?.let { System(systemEntity) },
        )
    }
}