package com.gitub.oopgurus.refactoringproblems.configserver

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ConfigEditService(
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository,
    private val configRepository: ConfigRepository,
) {

    @Transactional
    fun editConfig(id: String, configEditDto: ConfigEditDto) {
        val editConfig = EditConfig(
            configId = id.toLong(),
            configRepository = configRepository,
            personRepository = personRepository,
            systemRepository = systemRepository
        )

        editConfig.firstName(configEditDto.personFirstName)
        editConfig.lastName(configEditDto.personLastName)
        editConfig.phone(configEditDto.personPhone)
        editConfig.on(configEditDto.systemOn)
        editConfig.notes(configEditDto.systemNotes)
        editConfig.properties(configEditDto.properties)

        editConfig.reflectDataBase()
    }


}
