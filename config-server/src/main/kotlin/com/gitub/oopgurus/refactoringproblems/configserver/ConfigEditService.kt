package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ConfigEditService(
    private val personRepository: PersonRepository,
    private val systemRepository: SystemRepository,
    private val configRepository: ConfigRepository,
    private val configBuilderProvider: ObjectProvider<ConfigBuilder>,
) {

    private val objectMapper = ObjectMapper()

    @Transactional
    fun editConfig(id: String, configEditDto: ConfigEditDto) {
        val configBuilder = configBuilderProvider.getObject()
        configRepository.findById(id.toLong()).get().let {
            val config = configBuilder
                .id(it.id!!)
                .properties(it.properties)
                .build()
        }

        // 이건 내 의견이니 무시해..ㅎ
        // config 의 persons
        // config 의 system
        // 그 값에 따라서 동작
        // 새로운 visitor를 하나 만들고 그것을
        // 기존에 getDto 만들던 것 처럼 값을 검증하고 이번에는 로직을 수행하도록 하면 될듯

        // ?? 역으로.,,.?
        // 의사소통 방식을 정해준다.
        if (configEditDto.personFirstName != null) {
            personRepository.findAllByConfigId(id.toLong()).forEach {
                it.firstName = configEditDto.personFirstName
                personRepository.save(it)
            }
        }
        if (configEditDto.personLastName != null) {
            personRepository.findAllByConfigId(id.toLong()).forEach {
//                if -> 왜 부적합하니. Person의 내용을 바꾸는 Person 에 대한 도메인 로직이니까 Person 이 적합
//                validator -> validate 호출을 까먹을 수 있다? 유효한 객체가 만들어지는 것이 보장되지 않음
//                항상 유효한 객체만 만들어져야하기에 validator 는 나올 수 없음
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
