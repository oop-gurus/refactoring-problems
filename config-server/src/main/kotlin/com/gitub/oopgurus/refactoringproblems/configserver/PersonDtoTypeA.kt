package com.gitub.oopgurus.refactoringproblems.configserver

data class PersonDtoTypeA(
    val id: Long,

    // true이면 isForeigner=false이어야 함
    val isKorean: Boolean?,

    // true이면 name은 firstName + lastName 이어야 함.
    // false이면 name은 lastName + firstName 이어야함
    val isForeigner: Boolean?,

    val name: String,
    val firstName: String?,
    val lastName: String?,

    // 이메일 형식이어야 함
    val email: String?,

    // true이면 phone field 형식은 010-xxxx-yyyy
    val isMobilePhone: Boolean?,
    // true이면 phone field 형식은 02-xxxx-yyyy
    val isOfficePhone: Boolean?,
    val phone: String?, //
)

// TODO : !!! 한국인과 first name, last name 뒤집어서 줘야한다.
data class ForeignerDto(
    val id: Long,

    // true이면 isForeigner=false이어야 함
    val isKorean: Boolean?,

    // true이면 name은 firstName + lastName 이어야 함.
    // false이면 name은 lastName + firstName 이어야함
    val isForeigner: Boolean?,

    val name: String,

    //
    val firstName: String?,
    val lastName: String?,

    // 이메일 형식이어야 함
    val email: String?,

    // true이면 phone field 형식은 010-xxxx-yyyy
    val isMobilePhone: Boolean?,
    // true이면 phone field 형식은 02-xxxx-yyyy
    val isOfficePhone: Boolean?,
    val phone: String?, //
)


data class PersonDtoTypeC(
    val id: Long,
    val name: String
)

interface PersonDtoBuilder {
    fun 나_넘겨줄테니까_너가_값_채워넣어(person: Person)
}


class PersonDtoTypeABuilders : PersonDtoBuilder {
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
    override fun 나_넘겨줄테니까_너가_값_채워넣어(person: Person) {
        idGet = { person.id() }
        nameGet = { person.name() }
        firstNameGet = { person.firstName() }
        lastNameGet = { person.lastName() }
        emailGet = { person.email() }
        phoneGet = { person.phone() }
        isKoreanGet = { person.isKorean() }
        isForeignerGet = { person.isForeigner() }
        isMobilePhoneGet = { person.isMobilePhone() }
        isOfficePhoneGet = { person.isOfficePhone() }
    }

    fun result(): PersonDtoTypeA {
        return PersonDtoTypeA(
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

class ForeignerDtoBuilders : PersonDtoBuilder {
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
    override fun 나_넘겨줄테니까_너가_값_채워넣어(person: Person) {
        idGet = { person.id() }
        nameGet = { person.name() }
        firstNameGet = { person.firstName() }
        lastNameGet = { person.lastName() }
        emailGet = { person.email() }
        phoneGet = { person.phone() }
        isKoreanGet = { person.isKorean() }
        isForeignerGet = { person.isForeigner() }
        isMobilePhoneGet = { person.isMobilePhone() }
        isOfficePhoneGet = { person.isOfficePhone() }
    }

    fun result(): ForeignerDto {
        return ForeignerDto(
            id = idGet(),
            name = nameGet(),
            firstName = lastNameGet(),
            lastName = firstNameGet(),
            email = emailGet(),
            phone = phoneGet(),
            isKorean = isKoreanGet(),
            isForeigner = isForeignerGet(),
            isMobilePhone = isMobilePhoneGet(),
            isOfficePhone = isOfficePhoneGet(),
        )
    }
}

class PersonDtoTypeCBuilders : PersonDtoBuilder {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var nameGet: () -> String = { throw IllegalStateException() }
    override fun 나_넘겨줄테니까_너가_값_채워넣어(person: Person) {
        idGet = { person.id() }
        nameGet = { person.name() }
    }

    fun result(): PersonDtoTypeC {
        return PersonDtoTypeC(
            id = idGet(),
            name = nameGet(),
        )
    }
}


interface TestPersonDtoInterface {
    fun getDto(personDtoBuilder: PersonDtoBuilder)
}
