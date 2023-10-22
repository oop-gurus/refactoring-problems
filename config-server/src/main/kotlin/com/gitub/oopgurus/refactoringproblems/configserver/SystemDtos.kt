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

}

class SystemDtoBuilder {
    fun getDto(system: System): SystemDto {
        return SystemDto(
            id = system.id,
            on = system.on,
            off = system.on.not(),
            notes = system.notes
        )
    }
}

data class SystemDto(
    val id: Long,
    val on: Boolean?, // on=true이면 off=false이어야 함
    val off: Boolean?, // on=false이면 on=true이어야 함
    val notes: String?, // on=true이면 notes가 non-null 이어야 함
)