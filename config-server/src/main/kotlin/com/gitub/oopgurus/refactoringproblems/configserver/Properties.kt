package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper

class Properties(
    val holder: Map<String, String>,
) {
    companion object {
        private val objectMapper = ObjectMapper()

        fun parse(value: String): Properties {
            return Properties(objectMapper.readValue(value, Map::class.java) as Map<String, String>)
        }
    }

    fun descriptions(): List<String> {
        return holder
            .filterKeys { it.startsWith("PROPS_DESCRIPTION_") }
            .values
            .toList()
    }
}
