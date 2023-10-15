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
            val config = configBuilder
                .id(it.id!!)
                .properties(it.properties)
                .build()
            config.accept(configGetDtoBuilder)
            configGetDtoBuilder.build()
        }
    }

    fun getAllConfigs(): List<ConfigGetDto> {
        return personRepository
            .findAll()
            .map { getConfig(it.id!!) }
    }
}
