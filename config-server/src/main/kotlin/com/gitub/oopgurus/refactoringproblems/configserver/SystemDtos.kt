package com.gitub.oopgurus.refactoringproblems.configserver

class System(
    private val systemEntity: SystemEntity
) {
    // TODO : nullable 체크 안함 (스펙 모르겠음)
    val id: Long = systemEntity.id!!
    val on = systemEntity.on!!
    val notes = systemEntity.notes!!
    val configId = systemEntity.configId!!
    val updatedAt = systemEntity.updatedAt!!
    val createdAt = systemEntity.createdAt!!

    fun okay_i_will_give_you_what_you_want(systemDtoSupplier: SystemDtoSupplier) {
        systemDtoSupplier.값_채우기(this)
    }

}

class SystemDtoSupplier {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var onGet: () -> Boolean? = { throw IllegalStateException() }
    private var offGet: () -> Boolean? = { throw IllegalStateException() }
    private var notesGet: () -> String? = { throw IllegalStateException() }

    fun 값_채우기(system: System) {
        idGet = { system.id }
        onGet = { system.on }
        offGet = { system.on.not() }
        notesGet = { system.notes }
    }

    fun result(): SystemDto {
        return SystemDto(
            id = idGet(),
            on = onGet(),
            off = offGet(),
            notes = notesGet(),
        )
    }
}

data class SystemDto(
    val id: Long,
    val on: Boolean?, // on=true이면 off=false이어야 함
    val off: Boolean?, // on=false이면 on=true이어야 함
    val notes: String?, // on=true이면 notes가 non-null 이어야 함
)