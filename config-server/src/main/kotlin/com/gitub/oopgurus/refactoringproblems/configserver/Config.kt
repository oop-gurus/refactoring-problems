package com.gitub.oopgurus.refactoringproblems.configserver

class Config(
    private val id: Long,
    private val properties: Properties,
    private val persons: Persons,
    private val system: System?,
): Element {
    override fun accept(configVisitor: ConfigVisitor) {
        persons.accept(configVisitor)
        system?.accept(configVisitor)
        properties.accept(configVisitor)
        configVisitor.id(id)
    }
}