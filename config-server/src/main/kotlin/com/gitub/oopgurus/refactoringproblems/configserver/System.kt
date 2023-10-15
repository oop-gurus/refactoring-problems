package com.gitub.oopgurus.refactoringproblems.configserver

class System(
    private val entity: SystemEntity,
): Element {
    fun okay_i_will_give_you_what_you_want(systemVisitor: SystemVisitor) {
        systemVisitor.id(id())
        systemVisitor.on(on())
        systemVisitor.notes(notes())
    }

    private fun id(): Long = entity.id!!

    private fun on(): Boolean = entity.on!!

    private fun notes(): String = entity.notes!!
    override fun accept(configVisitor: ConfigVisitor) {
        configVisitor.system(this)
    }
}
