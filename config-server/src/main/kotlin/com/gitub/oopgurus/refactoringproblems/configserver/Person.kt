package com.gitub.oopgurus.refactoringproblems.configserver

class Person(
    private val entity: PersonEntity,
): DomainObject {

    override fun okay_i_will_give_you_what_you_want(whatIWantToPerson: WhatIWantToPerson) {
        whatIWantToPerson.name(name())
        whatIWantToPerson.firstName(firstName())
        whatIWantToPerson.lastName(lastName())
        whatIWantToPerson.email(email())
        whatIWantToPerson.phone(phone())
        whatIWantToPerson.isKorean(isKorean())
        whatIWantToPerson.isMobilePhone(isMobilePhone())
        whatIWantToPerson.isOfficePhone(isOfficePhone())
    }

    override fun okay_i_will_give_you_what_you_want(whatIWantToSystem: WhatIWantToSystem) {
        // nothing
    }

    override fun okay_i_will_give_you_what_you_want(whatIWantToProperties: WhatIWantToProperties) {
        // nothing
    }

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
}

