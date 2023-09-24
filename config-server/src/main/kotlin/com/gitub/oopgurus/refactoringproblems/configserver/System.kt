package com.gitub.oopgurus.refactoringproblems.configserver

class System(
    private val entity: SystemEntity,
): DomainObject {
    override fun okay_i_will_give_you_what_you_want(whatIWantToPerson: WhatIWantToPerson) {
        // nothing
    }

    override fun okay_i_will_give_you_what_you_want(whatIWantToSystem: WhatIWantToSystem) {
        whatIWantToSystem.id(id())
        whatIWantToSystem.on(on())
        whatIWantToSystem.notes(notes())
    }

    override fun okay_i_will_give_you_what_you_want(whatIWantToProperties: WhatIWantToProperties) {
        // nothing
    }

    private fun id(): Long = entity.id!!

    private fun on(): Boolean = entity.on!!

    private fun notes(): String = entity.notes!!
}
