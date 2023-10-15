package com.gitub.oopgurus.refactoringproblems.configserver

interface ConfigVisitor {
    fun id(id: Long)
    fun person(person: Person)
    fun descriptions(descriptions: List<String>)

    fun properties(properties: Map<String, String>)

    fun system(system: System)
}