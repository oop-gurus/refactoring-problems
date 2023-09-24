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

        return configRepository.findById(id).get().let {
            val builder = ConfigGetDtoBuilder(system, persons)
            Config(it).okay_i_will_give_you_what_you_want(builder)
            builder.build()
        }
    }

    fun getAllConfigs(): List<ConfigGetDto> {
        return personRepository
            .findAll()
            .map { getConfig(it.id!!) }
    }
}
