package com.gitub.oopgurus.refactoringproblems.configserver

class Person(
    private val entity: PersonEntity,
): Element {

    fun okay_i_will_give_you_what_you_want(personVisitor: PersonVisitor) {
        personVisitor.id(id())
        personVisitor.name(name())
        personVisitor.firstName(firstName())
        personVisitor.lastName(lastName())
        personVisitor.email(email())
        personVisitor.phone(phone())
        personVisitor.isKorean(isKorean())
        personVisitor.isMobilePhone(isMobilePhone())
        personVisitor.isOfficePhone(isOfficePhone())
    }

    private fun id() = entity.id!!

    private fun name(): String {
        return if (isKorean()) {
            "${firstName()}${lastName()}"
        } else {
            "${lastName()}${firstName()}"
        }
    }

    private fun lastName() = entity.lastName!!

    private fun firstName() = entity.firstName!!

    private fun email(): String {
        return entity.email!!
    }

    private fun phone(): String {
        return entity.phone!!
    }

    private fun isKorean(): Boolean {
        return entity.firstName!!.matches(Regex("[가-힣]+"))
    }

    private fun isForeigner(): Boolean {
        return !isKorean()
    }

    private fun isMobilePhone(): Boolean {
        return entity.phone!!.startsWith("010")
    }

    private fun isOfficePhone(): Boolean {
        return entity.phone!!.startsWith("02")
    }

    override fun accept(configVisitor: ConfigVisitor) {
        configVisitor.person(this)
    }
}

