package com.gitub.oopgurus.refactoringproblems.configserver

class Person(
    private val id: Long,
    private val firstName: String,
    private val lastName: String,
    private val email: String,
    private val phone: String,
): Element {

    fun okay_i_will_give_you_what_you_want(personVisitor: PersonVisitor) {
        personVisitor.id(id)
        personVisitor.name(name())
        personVisitor.firstName(firstName)
        personVisitor.lastName(lastName)
        personVisitor.email(email)
        personVisitor.phone(phone)
        personVisitor.isKorean(isKorean())
        personVisitor.isMobilePhone(isMobilePhone())
        personVisitor.isOfficePhone(isOfficePhone())
    }

    private fun name(): String {
        return if (isKorean()) {
            "${firstName}${lastName}"
        } else {
            "${lastName}${firstName}"
        }
    }
    private fun isKorean(): Boolean {
        return firstName.matches(Regex("[가-힣]+"))
    }

    private fun isForeigner(): Boolean {
        return !isKorean()
    }

    private fun isMobilePhone(): Boolean {
        return phone.startsWith("010")
    }

    private fun isOfficePhone(): Boolean {
        return phone.startsWith("02")
    }

    override fun accept(configVisitor: ConfigVisitor) {
        configVisitor.person(this)
    }
}

