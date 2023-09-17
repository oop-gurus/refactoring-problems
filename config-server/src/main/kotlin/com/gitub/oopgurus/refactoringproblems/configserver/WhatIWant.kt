package com.gitub.oopgurus.refactoringproblems.configserver

class WhatIWant {
    private var descriptionsGet: () -> List<String> = { throw IllegalStateException("descriptions 설정이 안되어있음") }
    private var propertiesGet: () -> Map<String, String> = { throw IllegalStateException("properties 설정이 안되어있음") }

    fun getDescriptions(): List<String> {
        return descriptionsGet()
    }

    fun setDescriptions(descriptions: List<String>) {
        descriptionsGet = { descriptions }
    }

    fun getProperties(): Map<String, String> {
        return propertiesGet()
    }

    fun setProperties(properties: Map<String, String>) {
        propertiesGet = { properties }
    }
}
