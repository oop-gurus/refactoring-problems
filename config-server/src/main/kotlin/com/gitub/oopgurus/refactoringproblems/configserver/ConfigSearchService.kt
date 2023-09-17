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
            var isMobilePhone = false
            var isOfficePhone = false
            if (it.phone!!.startsWith("010")) {
                isMobilePhone = true
            }
            if (it.phone!!.startsWith("02")) {
                isOfficePhone = true
            }

            // check firstname has alphabet
            val isKorean = it.firstName!!.matches(Regex("[가-힣]+"))

            PersonDto(
                id = it.id!!,
                name = if (isKorean) {
                    "${it.firstName}${it.lastName}"
                } else {
                    "${it.lastName}${it.firstName}"
                },
                email = it.email,
                phone = it.phone,
                isForeigner = !isKorean,
                isKorean = isKorean,
                firstName = it.firstName!!,
                lastName = it.lastName!!,
                isMobilePhone = isMobilePhone,
                isOfficePhone = isOfficePhone,
            )
        }
        val system = systemRepository.findByConfigId(id)?.let {
            SystemDto(
                id = it.id!!,
                on = it.on!!,
                off = !it.on!!,
                notes = it.notes!!,
            )
        }


        val config = configRepository.findById(id).get()
        val properties = Properties.parse(config.properties)

        val whatIWant = WhatIWant()
        properties.okay_i_will_give_you_what_you_want(whatIWant)
        return ConfigGetDto(
            id = config.id!!,
            isValidSystem = system != null,
            system = system,
            persons = persons,
            properties = whatIWant.getProperties(),
            descriptions = whatIWant.getDescriptions(),
        )
    }

    fun getAllConfigs(): List<ConfigGetDto> {
        return personRepository
            .findAll()
            .map { getConfig(it.id!!) }
    }
}
