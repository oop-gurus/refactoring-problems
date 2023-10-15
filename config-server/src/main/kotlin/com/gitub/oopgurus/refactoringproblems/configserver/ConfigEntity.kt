package com.gitub.oopgurus.refactoringproblems.configserver

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
class ConfigEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    var properties: String,

    @Column
    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null,

    @Column
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
) {
    fun okay_i_will_give_you_what_you_want(configBuilder: ConfigBuilder) {
        configBuilder.id(id!!)
        configBuilder.properties(properties)
    }
}
