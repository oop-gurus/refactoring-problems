package com.gitub.oopgurus.refactoringproblems.configserver

class Person(
    private val entity: PersonEntity,
) {

    fun okay_i_will_give_you_what_you_want(personDtoBuilder: PersonDtoBuilder) {
        personDtoBuilder.name(name())
        personDtoBuilder.firstName(firstName())
        personDtoBuilder.lastName(lastName())
        personDtoBuilder.email(email())
        personDtoBuilder.phone(phone())
        personDtoBuilder.isKorean(isKorean())
        personDtoBuilder.isMobilePhone(isMobilePhone())
        personDtoBuilder.isOfficePhone(isOfficePhone())
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

class PersonDtoBuilder {
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

    fun name(name: String) {
        nameGet = { name }
    }

    fun firstName(firstName: String) {
        firstNameGet = { firstName }
    }

    fun lastName(lastName: String) {
        lastNameGet = { lastName }
    }

    fun email(email: String) {
        emailGet = { email }
    }

    fun phone(phone: String) {
        phoneGet = { phone }
    }

    fun isKorean(isKorean: Boolean) {
        isKoreanGet = { isKorean }
        isForeignerGet = { !isKorean }
    }

    fun isMobilePhone(isMobilePhone: Boolean) {
        isMobilePhoneGet = { isMobilePhone }
    }

    fun isOfficePhone(isOfficePhone: Boolean) {
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
