package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

interface ConfigVisitor {
    fun firstName(firstName: String?)
    fun lastName(lastName: String?)
    fun phone(phone: String?)
    fun on(on: Boolean?)
    fun notes(notes: String?)
    fun properties(properties: Map<String, String>?)
}


class EditConfig(
    private val configId: Long,
    private val configRepository: ConfigRepository,
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository
) : ConfigVisitor {
    private val config by lazy { configRepository.findById(configId).get() }
    private val persons by lazy { personRepository.findAllByConfigId(configId) }
    private val system by lazy { systemRepository.findByConfigId(configId) }

    private val objectMapper = ObjectMapper()


    override fun firstName(firstName: String?) {
        if (firstName == null) return
        persons.forEach { it.firstName = firstName }
    }

    override fun lastName(lastName: String?) {
        if (lastName == null) return
        persons.forEach { it.lastName = lastName }
    }

    override fun phone(phone: String?) {
        if (phone == null) return
        persons.forEach { it.phone = phone }
    }

    override fun on(on: Boolean?) {
        if (on == null) return
        system?.let {
            it.on = on
        }
    }

    override fun notes(notes: String?) {
        if (notes == null) return
        system?.let {
            it.notes = notes
        }
    }

    override fun properties(properties: Map<String, String>?) {
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


class Keyword(value: String) {

}


@Component
class KeywordFactory(
    private val maximumLength: Int = 10
) {
    fun create(value: String): Keyword {
        if (value.length > maximumLength) {
            throw IllegalArgumentException("키워드는 최대 $maximumLength 글자까지 가능합니다.")
        }
        return Keyword(value)
    }
}
