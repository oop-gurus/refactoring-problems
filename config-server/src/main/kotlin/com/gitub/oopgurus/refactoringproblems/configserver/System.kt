package com.gitub.oopgurus.refactoringproblems.configserver

class System(
    private val id: Long,
    private val on: Boolean,
    private val notes: String,
): Element {
    fun okay_i_will_give_you_what_you_want(systemVisitor: SystemVisitor) {
        systemVisitor.id(id)
        systemVisitor.on(on)
        systemVisitor.notes(notes)
    }

    override fun accept(configVisitor: ConfigVisitor) {
        configVisitor.system(this)
    }
}
