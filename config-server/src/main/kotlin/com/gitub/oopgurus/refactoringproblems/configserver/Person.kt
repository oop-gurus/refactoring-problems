package com.gitub.oopgurus.refactoringproblems.configserver

class Person(
    private val entity: PersonEntity,
) {

    fun okay_i_will_give_you_what_you_want(whatIWantToPerson: WhatIWantToPerson) {
        whatIWantToPerson.name(name())
        whatIWantToPerson.firstName(firstName())
        whatIWantToPerson.lastName(lastName())
        whatIWantToPerson.email(email())
        whatIWantToPerson.phone(phone())
        whatIWantToPerson.isKorean(isKorean())
        whatIWantToPerson.isMobilePhone(isMobilePhone())
        whatIWantToPerson.isOfficePhone(isOfficePhone())
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

interface WhatIWantToPerson {
    fun name(name: String)

    fun firstName(firstName: String)

    fun lastName(lastName: String)

    fun email(email: String)

    fun phone(phone: String)

    fun isKorean(isKorean: Boolean)

    fun isMobilePhone(isMobilePhone: Boolean)

    fun isOfficePhone(isOfficePhone: Boolean)
}

class PersonDtoBuilder: WhatIWantToPerson {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var nameGet: () -> String = { throw IllegalStateException() }
    private var firstNameGet: () -> String = { throw IllegalStateException() }
    private var lastNameGet: () -> String = { throw IllegalStateException() }
    private var emailGet: () -> String = { throw IllegalStateException() }
    private var phoneGet: () -> String = { throw IllegalStateException() }
    private var isKoreanGet: () -> Boolean = { throw IllegalStateException() }
    private var isForeignerGet: () -> Boolean = { throw IllegalStateException() }
    private var isMobilePhoneGet: () -> Boolean = { throw IllegalStateException() }
    private var isOfficePhoneGet: () -> Boolean = { throw IllegalStateException() }

    override fun name(name: String) {
        nameGet = { name }
    }

    override fun firstName(firstName: String) {
        firstNameGet = { firstName }
    }

    override fun lastName(lastName: String) {
        lastNameGet = { lastName }
    }

    override fun email(email: String) {
        emailGet = { email }
    }

    override fun phone(phone: String) {
        phoneGet = { phone }
    }

    override fun isKorean(isKorean: Boolean) {
        isKoreanGet = { isKorean }
        isForeignerGet = { !isKorean }
    }

    override fun isMobilePhone(isMobilePhone: Boolean) {
        isMobilePhoneGet = { isMobilePhone }
    }

    override fun isOfficePhone(isOfficePhone: Boolean) {
        isOfficePhoneGet = { isOfficePhone }
    }

    fun result(): PersonDto {
        return PersonDto(
            id = idGet(),
            name = nameGet(),
            firstName = firstNameGet(),
            lastName = lastNameGet(),
            email = emailGet(),
            phone = phoneGet(),
            isKorean = isKoreanGet(),
            isForeigner = isForeignerGet(),
            isMobilePhone = isMobilePhoneGet(),
            isOfficePhone = isOfficePhoneGet(),
        )
    }
}
