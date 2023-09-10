package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ConfigEditService(
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository,
    private val configRepository: ConfigRepository,
) {

    private val objectMapper = ObjectMapper()

    @Transactional
    fun editConfig(id: String, configEditDto: ConfigEditDto) {
        if (configEditDto.personFirstName != null) {
            personRepository.findAllByConfigId(id.toLong()).forEach {
                it.firstName = configEditDto.personFirstName
                personRepository.save(it)
            }
        }
        if (configEditDto.personLastName != null) {
            personRepository.findAllByConfigId(id.toLong()).forEach {
                it.lastName = configEditDto.personLastName
                personRepository.save(it)
            }
        }
        if (configEditDto.personPhone != null) {
            personRepository.findAllByConfigId(id.toLong()).forEach {
                it.phone = configEditDto.personPhone
                personRepository.save(it)
            }
        }

        if (configEditDto.systemOn != null) {
            systemRepository.findByConfigId(id.toLong())?.let {
                it.on = configEditDto.systemOn
                systemRepository.save(it)
            }
        }

        if (configEditDto.systemNotes != null) {
            systemRepository.findByConfigId(id.toLong())?.let {
                it.notes = configEditDto.systemNotes
                systemRepository.save(it)
            }
        }


        val configEntity = configRepository.findById(id.toLong()).get()
        val originProperties = objectMapper.readValue(configEntity.properties, Map::class.java) as Map<String, String>
        val newProperties = configEditDto.properties ?: emptyMap()
        val merged = originProperties + newProperties
        configEntity.properties = objectMapper.writeValueAsString(merged)
    }
}
