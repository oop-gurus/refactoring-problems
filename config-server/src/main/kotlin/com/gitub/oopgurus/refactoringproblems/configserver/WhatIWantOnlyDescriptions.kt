package com.gitub.oopgurus.refactoringproblems.configserver

class WhatIWantOnlyDescriptions : WhatIWantToProperties {
    private var descriptionsGet: () -> List<String> = { throw IllegalStateException("descriptions 설정이 안되어있음") }

    fun getDescriptions(): List<String> {
        return descriptionsGet()
    }

    override fun descriptions(descriptions: List<String>) {
        descriptionsGet = { descriptions }
    }

    override fun properties(properties: Map<String, String>) {
        // 실제로는 필요 없으므로 아무것도 안 하면 됨
        // do nothing
    }
}