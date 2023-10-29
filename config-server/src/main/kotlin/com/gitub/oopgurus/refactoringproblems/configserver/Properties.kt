package com.gitub.oopgurus.refactoringproblems.configserver

import com.fasterxml.jackson.databind.ObjectMapper

interface 정보를_가진놈 {
    fun 너한테_내정보_허락해줄께_정보_넘겨줄테니_너가_값_채워넣어(도둑: 정보를_훔쳐갈놈)
}

interface Person_서비스_센터 {
    fun 원하는_정보만_채우기(info: Person)
}

class Properties(
    private val holder: Map<String, String>,
): 정보를_가진놈 {
    companion object {
        private val objectMapper = ObjectMapper()

        fun parse(value: String): Properties {
            return Properties(objectMapper.readValue(value, Map::class.java) as Map<String, String>)
        }
    }

    fun okay_i_will_give_you_what_you_want(whatIWant: WhatIWant) {
        whatIWant.descriptions(descriptions())
        whatIWant.properties(holder)
    }

    private fun descriptions(): List<String> {
        return holder
            .filterKeys { it.startsWith("PROPS_DESCRIPTION_") }
            .values
            .toList()
    }

    override fun 너한테_내정보_허락해줄께_정보_넘겨줄테니_너가_값_채워넣어(도둑: 정보를_훔쳐갈놈) {
        // properties 타입 케스틍 된다.
        // 하지만 descriptions() 는?
        도둑.원하는_정보만_채우기(this)
    }
}

