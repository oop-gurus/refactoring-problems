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

    fun id(id: Long): ConfigBuilder {
        idGet = { id }
        return this
    }

    fun properties(properties: String): ConfigBuilder {
        propertiesGet = { Properties.parse(properties) }
        return this
    }

    fun build(): Config {
        return Config(
            id = idGet(),
            properties = propertiesGet(),
            persons = makePersons(),
            system = makeSystem(),
        )
    }

    private fun makePersons() = personRepository.findAllByConfigId(idGet()).map {
        Person(
            id = it.id!!,
            firstName = it.firstName!!,
            lastName = it.lastName!!,
            email = it.email!!,
            phone = it.phone!!,
        )
    }.let { Persons(it) }

    private fun makeSystem() = systemRepository.findByConfigId(idGet())?.let {
        System(
            id = it.id!!,
            on = it.on!!,
            notes = it.notes!!,
        )
    }
}