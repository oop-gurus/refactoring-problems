package com.gitub.oopgurus.refactoringproblems.configserver

class Config(
    private val entity: ConfigEntity,
) {
    fun okay_i_will_give_you_what_you_want(whatIWantToConfig: WhatIWantToConfig) {
        whatIWantToConfig.id(id())
        val whatIWantBothToProperties = WhatIWantBothToProperties()
        properties().okay_i_will_give_you_what_you_want(whatIWantBothToProperties)
        whatIWantToConfig.properties(whatIWantBothToProperties.getProperties())
        whatIWantToConfig.descriptions(whatIWantBothToProperties.getDescriptions())
    }

    private fun id(): Long = entity.id!!
    private fun properties(): Properties = Properties.parse(entity.properties)
}