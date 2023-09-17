package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper

class Properties(
    private val holder: Map<String, String>,
) {
    companion object {
        private val objectMapper = ObjectMapper()

        fun parse(value: String): Properties {
            return Properties(objectMapper.readValue(value, Map::class.java) as Map<String, String>)
        }
    }

    // ConfigSearchService(외부): Properties(내부)의 필드에 접근이 필요함
    // Properties(내부): 내 정보를 외부에 보여줘야 하는건 알겠는데, 어떤 정보가 필요할지 모르니 다 열어줘야함
    // 아이디어 -> Properties(내부)가 어떤 정보를 주는지 결정하면 안될까?
    // 문제점: 표현의 가짓수가 많아질수록 return 값으로 표현하는게 너무 힘들다... 어떡하지?
    //   -> 일단 map으로 표현하면 좀 낫다
    // 문제점2: 이제는 외부에서 달라고하는거 Properties(내부)가 어떤 정보를 주는지 결정하긴 하는데,
    //   What_I_Want 이거 좀 어떻게 안되나...? 매번 이런식으로 해야하나?
    //   -> 역시 우리의 해결책은 항상 객체다
    fun okay_i_will_give_you_what_you_want(whatIWant: What_I_Want) {
        whatIWant.setDescriptions(descriptions())
        whatIWant.setProperties(holder)
    }

    private fun descriptions(): List<String> {
        return holder
            .filterKeys { it.startsWith("PROPS_DESCRIPTION_") }
            .values
            .toList()
    }
}

class What_I_Want {
    fun getDescriptions(): List<String> {
        TODO()
    }

    fun setDescriptions(descriptions: List<String>) {
        TODO()
    }

    fun getProperties(): Map<String, String> {
        TODO()
    }

    fun setProperties(properties: Map<String, String>) {
        TODO()
    }
}
