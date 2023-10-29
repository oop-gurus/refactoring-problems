package com.gitub.oopgurus.refactoringproblems.configserver

class System(
    private val systemEntity: SystemEntity
): 정보를_가진놈 {
    // TODO : nullable 체크 안함 (스펙 모르겠음)

    // 1 System을 외부에 노출하고 싶지 않다. private method
    private val id: Long = systemEntity.id!!
    val on = systemEntity.on!!
    val notes = systemEntity.notes!!
    val configId = systemEntity.configId!!
    val updatedAt = systemEntity.updatedAt!!
    val createdAt = systemEntity.createdAt!!

    override fun 너한테_내정보_허락해줄께_정보_넘겨줄테니_너가_값_채워넣어(도둑: 정보를_훔쳐갈놈) {
        // system으로 타입 케스팅이 된다.
        도둑.원하는_정보만_채우기(this)
    }

}

class SystemDtoBuilder: 정보를_훔쳐갈놈 {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var onGet: () -> Boolean? = { throw IllegalStateException() }
    private var offGet: () -> Boolean? = { throw IllegalStateException() }
    private var notesGet: () -> String? = { throw IllegalStateException() }

    fun result(): SystemDto {
        return SystemDto(
            id = idGet(),
            on = onGet(),
            off = offGet(),
            notes = notesGet(),
        )
    }

    override fun 원하는_정보만_채우기(info: 정보를_가진놈) {
        // 핵 구림
        val system = info as System

        idGet = { system.id }
        onGet = { system.on }
        offGet = { system.on.not() }
        notesGet = { system.notes }
    }
}

data class SystemDto(
    val id: Long,
    val on: Boolean?, // on=true이면 off=false이어야 함
    val off: Boolean?, // on=false이면 on=true이어야 함
    val notes: String?, // on=true이면 notes가 non-null 이어야 함
)