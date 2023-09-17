package com.gitub.oopgurus.refactoringproblems.configserver

class Person(
    private val entity: PersonEntity,
) {

    private fun name(): String {
        return if (isKorean()) {
            "${entity.firstName}${entity.lastName}"
        } else {
            "${entity.lastName}${entity.firstName}"
        }
    }

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
