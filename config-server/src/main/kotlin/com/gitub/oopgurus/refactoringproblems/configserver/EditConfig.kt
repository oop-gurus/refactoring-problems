package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper

class EditConfig(
    private val configId: Long,
    private val configRepository: ConfigRepository,
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository
) {
    private val config by lazy { configRepository.findById(configId).get() }
    private val persons by lazy { personRepository.findAllByConfigId(configId) }
    private val system by lazy { systemRepository.findByConfigId(configId) }

    private val objectMapper = ObjectMapper()


    fun fixFirstName(firstName: String?) {
        if (firstName == null) return
        persons.forEach { it.firstName = firstName }
    }

    fun fixLastName(lastName: String?) {
        if (lastName == null) return
        persons.forEach { it.lastName = lastName }
    }

    fun fixPhone(phone: String?) {
        if (phone == null) return
        persons.forEach { it.phone = phone }
    }

    fun fixOn(on: Boolean?) {
        if (on == null) return
        system?.let {
            it.on = on
        }
    }

    fun fixNotes(notes: String?) {
        if (notes == null) return
        system?.let {
            it.notes = notes
        }
    }

    fun fixProperties(properties: Map<String, String>?) {
        if (properties == null) return
        val originProperties = objectMapper.readValue(config.properties, Map::class.java) as Map<String, String>

        val merged = originProperties + properties
        config.properties = objectMapper.writeValueAsString(merged)
    }

    fun reflectDataBase() {
        personRepository.saveAll(persons)
        system?.let { systemRepository.save(it) }
        configRepository.save(config)
    }
}