package com.gitub.oopgurus.refactoringproblems.configserver

class System(
    private val entity: SystemEntity,
) {

    fun okay_i_will_give_you_what_you_want(whatIWantToSystem: WhatIWantToSystem) {
        whatIWantToSystem.id(id())
        whatIWantToSystem.on(on())
        whatIWantToSystem.notes(notes())
    }

    private fun id(): Long = entity.id!!

    private fun on(): Boolean = entity.on!!

    private fun notes(): String = entity.notes!!
}


interface WhatIWantToSystem {
    fun id(id: Long)
    fun on(on: Boolean)
    fun notes(notes: String)
}

class SystemDtoBuilder: WhatIWantToSystem {
    private lateinit var idGet: () -> Long
    private lateinit var onGet: () -> Boolean
    private lateinit var offGet: () -> Boolean
    private lateinit var notesGet: () -> String

    override fun id(id: Long) {
        idGet = { id }
    }

    override fun on(on: Boolean) {
        onGet = { on }
        offGet = { !on }
    }

    override fun notes(notes: String) {
        notesGet = { notes }
    }

    fun build(): SystemDto {
        return SystemDto(
            id = idGet(),
            on = onGet(),
            off =  onGet(),
            notes = notesGet(),
        )
    }
}
