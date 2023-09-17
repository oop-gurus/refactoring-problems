package com.gitub.oopgurus.refactoringproblems.configserver

class WhatIWantBoth {
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

class WhatIWantOnlyDescriptions {
    private var descriptionsGet: () -> List<String> = { throw IllegalStateException("descriptions 설정이 안되어있음") }

    // 이건 ConfigSearchService에서 필요한 것
    fun getDescriptions(): List<String> {
        return descriptionsGet()
    }

    // 필요함
    fun setDescriptions(descriptions: List<String>) {
        descriptionsGet = { descriptions }
    }

    // 이건 ConfigSearchService에서 필요 없는 것
    fun getProperties(): Map<String, String> {
        TODO()
    }

    // 이건 실제로는 필요 없지만, Properties 입장에서는 필요한지 아닌지 알 수가 없음
    fun setProperties(properties: Map<String, String>) {
        TODO()
    }
}

class WhatIWantOnlyProperties {
    private var propertiesGet: () -> Map<String, String> = { throw IllegalStateException("properties 설정이 안되어있음") }

    // 이건 ConfigSearchService에서 필요 없는 것
    fun getDescriptions(): List<String> {
        TODO()
    }

    // 이건 실제로는 필요 없지만, Properties 입장에서는 필요한지 아닌지 알 수가 없음
    fun setDescriptions(descriptions: List<String>) {
        TODO()
    }

    // 이건 ConfigSearchService에서 필요한 것
    fun getProperties(): Map<String, String> {
        TODO()
    }

    // 필요함
    fun setProperties(properties: Map<String, String>) {
        propertiesGet = { properties }
    }
}
