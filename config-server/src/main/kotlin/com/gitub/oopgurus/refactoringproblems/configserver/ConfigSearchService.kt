package com.gitub.oopgurus.refactoringproblems.configserver

import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component
class ConfigSearchService(
    private val personRepository: PersonRepository,
    private val configBuilderProvider: ObjectProvider<ConfigBuilder>,
    private val configRepository: ConfigRepository,
) {
    fun getConfig(id: Long): ConfigGetDto {
        val configBuilder = configBuilderProvider.getObject()
        val configGetDtoBuilder = ConfigGetDtoBuilder()

        return configRepository.findById(id).get().let {
            it.okay_i_will_give_you_what_you_want(configBuilder)
            configBuilder
                .build()
                .accept(configGetDtoBuilder)
            configGetDtoBuilder.build()
        }
    }

    fun getAllConfigs(): List<ConfigGetDto> {
        return personRepository
            .findAll()
            .map { getConfig(it.id!!) }
    }
}
