package com.gitub.oopgurus.refactoringproblems.configserver

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