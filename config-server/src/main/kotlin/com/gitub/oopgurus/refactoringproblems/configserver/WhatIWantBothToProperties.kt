package com.gitub.oopgurus.refactoringproblems.configserver

class WhatIWantBothToProperties: WhatIWantToProperties {
    private var descriptionsGet: () -> List<String> = { throw IllegalStateException("descriptions 설정이 안되어있음") }
    private var propertiesGet: () -> Map<String, String> = { throw IllegalStateException("properties 설정이 안되어있음") }

    fun getDescriptions(): List<String> {
        return descriptionsGet()
    }

    override fun descriptions(descriptions: List<String>) {
        descriptionsGet = { descriptions }
    }

    fun getProperties(): Map<String, String> {
        return propertiesGet()
    }

    override fun properties(properties: Map<String, String>) {
        propertiesGet = { properties }
    }
}
