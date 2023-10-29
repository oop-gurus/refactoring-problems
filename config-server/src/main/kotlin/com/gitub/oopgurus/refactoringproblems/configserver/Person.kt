package com.gitub.oopgurus.refactoringproblems.configserver

class Person(
    private val entity: PersonEntity,
): 정보를_가진놈 {

    override fun 너한테_내정보_허락해줄께_정보_넘겨줄테니_너가_값_채워넣어(서비스_센터: 정보를_훔쳐갈놈) {
        /**
         * TODO : 그럼 Builder는 내가 무엇이 필요한지 알고 채워야한다.
         *
         *
         * 장/단점이 뭘까?
         * 상황 1. person에 age 데이터가 추가된다면?
         * 다른 builder는 영향을 받는가? -> 아니다. 추가가 필요한 dto builder에만 추가해주면 된다.
         * 결론: 단점 아님
         *
         * 상황 2. person의 타입이 동적으로 정해진다면?
         * 내 코드에서 korean일 경우 typeA를, foreigner일 경우 ForeignerDto 사용한다면?
         * -> 국적에 따라 name의 순서가 다르다고 할 때 해결 방법
         * 1. builder에서 해결
         * 2. person method에서 해결
         *
         * 1번 상황의 문제는 builder를 정하는 것은 person 클래스가 생성되기 전이다.
         * 여러 빌더중 어떤 타입을 사용할지 어떻게 정해줄 수 있지?
         *
         * 2. person에서 persondtobuilder들의 의존성은 없지만, 그걸 제공하기 위해 메소드에 동작이 변경되고 있다 (의존성이 숨어 있다고 느낌)
         * -> 이미 사용 하는 곳에서 사용하고 있다면 문제 가능 / 데이터 제공을 위한 동작이 다시 안으로 들어온다.
         *
         * 결론: 단점 인것 같음
         *
         * 상황 3. dto가 추가되거나 변경될 때 해당 부분만 수정해주면 된다.
         * 매우 큰 장점!! -> DTO의 종류가 매우 많을 때 좋을 것 같음
         *
         *
         *  추가
         *  1. 기존의 private fun이었던 것들 다 public으로 변경 됌 (ex: id(), name())
         */

        // this가 person으로 타입을 알 수 있음 (타입 캐스팅이 필요없어짐)
        서비스_센터.원하는_정보만_채우기(this)




        // 문제점2: PersonDtoBuilder는 PersonDto 에 대해서 알고 있는데...
        // 내가 그냥 만드는거랑 뭐가 달라?

        /**
         * https://thecodinglog.github.io/design/2019/10/29/visitor-pattern.html
         * 이거랑 너무 다르다. 혼란 그 잡체임;;
         * 비지터 패턴은 원래 묵시적 형변환을 제공하지 않아서 더블 디스패치 할려고 쓰는데
         * , 난 그런 부분도 없음
         *
         * visitor pattern은 행동 패턴인데, 내껄 보니 생성 패턴이네;
         */


    }

    fun id(): Long {
        return entity.id!!
    }


    fun name(): String {
        return if (isKorean()) {
            "${firstName()}${lastName()}"
        } else {
            "${lastName()}${firstName()}"
        }
    }

    fun lastName() = entity.lastName!!

    fun firstName() = entity.firstName!!

    fun email(): String {
        return entity.email!!
    }

    fun phone(): String {
        return entity.phone!!
    }

    fun isKorean(): Boolean {
        return entity.firstName!!.matches(Regex("[가-힣]+"))
    }

    fun isForeigner(): Boolean {
        return !isKorean()
    }

    fun isMobilePhone(): Boolean {
        return entity.phone!!.startsWith("010")
    }

    fun isOfficePhone(): Boolean {
        return entity.phone!!.startsWith("02")
    }
}

