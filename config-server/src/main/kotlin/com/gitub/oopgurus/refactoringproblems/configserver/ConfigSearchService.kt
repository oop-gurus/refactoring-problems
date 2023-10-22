package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ConfigSearchService(
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository,
    private val configRepository: ConfigRepository,
) {

    private val objectMapper = ObjectMapper()

    fun getConfig(id: Long): ConfigGetDto {
        val persons = personRepository.findAllByConfigId(id).map {
            val builder = PersonDtoTypeABuilders()
            Person(it).okay_i_will_give_you_what_you_want(builder)
            builder.result()
        }

        // config가 같이 누군가를 포함한다?
        val system = systemRepository.findByConfigId(id)?.let {
            // 1안
//            SystemDtoBuilder().getDto(System(it))

            // 2안용 (아직 장점을 모르겠음)
            val supplier = SystemDtoSupplier()
            System(it).okay_i_will_give_you_what_you_want(supplier)
            supplier.result()
        }


        val config = configRepository.findById(id).get()
        val properties = Properties.parse(config.properties)

        val whatIWantBoth = WhatIWantBoth()
        val whatIWantBoth2 = WhatIWantOnlyDescriptions()
        val whatIWantBoth3 = WhatIWantOnlyProperties()
        properties.okay_i_will_give_you_what_you_want(whatIWantBoth)

        // 문제3: 결국 이걸 원하는 건데...
        //   PersonDtoBuilder 같은 방식으로 ConfigGetDto가 나오면 안되려나...
        return ConfigGetDto(
            id = config.id!!,
            isValidSystem = system != null,
            system = system,
            persons = persons,
            properties = whatIWantBoth.getProperties(),
            descriptions = whatIWantBoth.getDescriptions(),
        )
    }

    fun getAllConfigs(): List<ConfigGetDto> {
        return personRepository
            .findAll()
            .map { getConfig(it.id!!) }
    }
}
