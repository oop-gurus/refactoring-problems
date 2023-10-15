package com.gitub.oopgurus.refactoringproblems.configserver

class Person(
    private val id: Long,
    private val firstName: String,
    private val lastName: String,
    private val email: String,
    private val phone: String,
): Element {

    fun accept(personVisitor: PersonVisitor) {
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

//    method -> 여기가 적절하겠지~?
    fun updateLastName(lastName: String) {
//        if 김이박최 throw ~

//        그런데 추가적으로!
//        새로운 요구사항: 외부 API 를 무조건 찔러서 사용 가능한 이름이어야 변경 가능 (의존성이 생기는 경우 - RestTemplate... 당연히 여기서 만들면 안되겠지..ㅎ)
//        1. 어떻게 할래?

//        2. ConfigEditService 에 도메인 레이어를 어떻게 넣을래?

//        3. edit 한다는 요구사항에 어떻게 visitor 패턴을 적용해볼래?
    }
}

