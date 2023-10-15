package com.gitub.oopgurus.refactoringproblems.configserver

class WhatIWantOnlyPropertiesVisitor: PropertiesVisitor {
    private var propertiesGet: () -> Map<String, String> = { throw IllegalStateException("properties 설정이 안되어있음") }

    override fun descriptions(descriptions: List<String>) {
        // 실제로는 필요 없으므로 아무것도 안 하면 됨
        // do nothing
    }

    // 이건 ConfigSearchService에서 필요한 것
    fun getProperties(): Map<String, String> {
        return propertiesGet()
    }

    // 필요함
    override fun properties(properties: Map<String, String>) {
        propertiesGet = { properties }
    }
}