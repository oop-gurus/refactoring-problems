package com.gitub.oopgurus.refactoringproblems.configserver

interface SystemVisitor {
    fun id(id: Long)
    fun on(on: Boolean)
    fun notes(notes: String)
}