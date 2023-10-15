package com.gitub.oopgurus.refactoringproblems.configserver

interface Element {
    // accept
    fun accept(configVisitor: ConfigVisitor)
}