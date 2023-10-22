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

        editConfig.fixFirstName(configEditDto.personFirstName)
        editConfig.fixLastName(configEditDto.personLastName)
        editConfig.fixPhone(configEditDto.personPhone)
        editConfig.fixOn(configEditDto.systemOn)
        editConfig.fixNotes(configEditDto.systemNotes)
        editConfig.fixProperties(configEditDto.properties)

        editConfig.reflectDataBase()
    }


}
