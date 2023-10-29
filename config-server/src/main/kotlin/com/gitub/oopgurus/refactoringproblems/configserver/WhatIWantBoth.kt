package com.gitub.oopgurus.refactoringproblems.configserver

interface WhatIWant {
    fun descriptions(descriptions: List<String>)

    fun properties(properties: Map<String, String>)
}

class WhatIWantBoth: WhatIWant {
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

class WhatIWantOnlyDescriptions : WhatIWant {
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

class WhatIWantOnlyProperties: WhatIWant {
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
