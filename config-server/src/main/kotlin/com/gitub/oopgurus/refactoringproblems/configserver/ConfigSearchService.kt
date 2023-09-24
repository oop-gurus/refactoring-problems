package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ConfigSearchService(
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository,
    private val configRepository: ConfigRepository,
) {
    fun getConfig(id: Long): ConfigGetDto {
        val persons = personRepository.findAllByConfigId(id).map {
            val builder = PersonDtoBuilder()
            Person(it).okay_i_will_give_you_what_you_want(builder)
            builder.result()
        }
        val system = systemRepository.findByConfigId(id)?.let {
            val builder = SystemDtoBuilder()
            System(it).okay_i_will_give_you_what_you_want(builder)
            builder.build()
        }

        val config = configRepository.findById(id).get()
        val properties = Properties.parse(config.properties)

        val whatIWantBothToProperties = WhatIWantBothToProperties()
        properties.okay_i_will_give_you_what_you_want(whatIWantBothToProperties)

        // 문제3: 결국 이걸 원하는 건데...
        //   PersonDtoBuilder 같은 방식으로 ConfigGetDto가 나오면 안되려나...
        return ConfigGetDto(
            id = config.id!!,
            isValidSystem = system != null,
            system = system,
            persons = persons,
            properties = whatIWantBothToProperties.getProperties(),
            descriptions = whatIWantBothToProperties.getDescriptions(),
        )
    }

    fun getAllConfigs(): List<ConfigGetDto> {
        return personRepository
            .findAll()
            .map { getConfig(it.id!!) }
    }
}
