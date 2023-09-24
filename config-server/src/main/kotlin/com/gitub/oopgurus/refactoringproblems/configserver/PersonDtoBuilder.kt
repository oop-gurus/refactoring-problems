package com.gitub.oopgurus.refactoringproblems.configserver

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

    fun build(): PersonDto {
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
