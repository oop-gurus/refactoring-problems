package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper

class Properties(
    private val holder: Map<String, String>,
): DomainObject {
    companion object {
        private val objectMapper = ObjectMapper()

        fun parse(value: String): Properties {
            return Properties(objectMapper.readValue(value, Map::class.java) as Map<String, String>)
        }
    }

    override fun okay_i_will_give_you_what_you_want(whatIWantToPerson: WhatIWantToPerson) {
        // nothing
    }

    override fun okay_i_will_give_you_what_you_want(whatIWantToSystem: WhatIWantToSystem) {
        // nothing
    }

    override fun okay_i_will_give_you_what_you_want(whatIWantToProperties: WhatIWantToProperties) {
        whatIWantToProperties.descriptions(descriptions())
        whatIWantToProperties.properties(holder)
    }

    private fun descriptions(): List<String> {
        return holder
            .filterKeys { it.startsWith("PROPS_DESCRIPTION_") }
            .values
            .toList()
    }
}

