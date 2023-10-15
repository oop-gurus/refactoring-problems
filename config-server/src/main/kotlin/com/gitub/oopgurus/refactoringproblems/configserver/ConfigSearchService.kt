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
        val configBuilderProvider = configBuilderProvider.getObject()
        val configGetDtoBuilder = ConfigGetDtoBuilder()

        return configRepository.findById(id).get().let {
            it.okay_i_will_give_you_what_you_want(configBuilderProvider)
            configBuilderProvider
                .build()
                .accept(configGetDtoBuilder)
            configGetDtoBuilder.build()
        }
    }

    // TODO:
    // 1. Config 를 만드는 방법을 고민해와라 (visitor랑 상관 있을 수도 없을 수도~)
    // 2. EditService 에도 해당 패턴을 최대한 활용해봐라! 재사용을 꼭 해야한다! 다 따로 만드는 것이 아니라! 재사용성을 고려하라! (이거에 더 집중하기!

    fun getAllConfigs(): List<ConfigGetDto> {
        return personRepository
            .findAll()
            .map { getConfig(it.id!!) }
    }
}
