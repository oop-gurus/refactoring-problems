package com.gitub.oopgurus.refactoringproblems.configserver

class Persons(
    private val list: List<Person>,
): Element {
    override fun accept(configVisitor: ConfigVisitor) {
        list.forEach { person -> configVisitor.person(person) }
    }
}