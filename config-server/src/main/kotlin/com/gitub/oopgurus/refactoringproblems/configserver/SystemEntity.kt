package com.gitub.oopgurus.refactoringproblems.configserver

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
class SystemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    var on: Boolean?,

    @Lob
    @Column
    var notes: String?,

    @Column
    var configId: Long?,

    @Column
    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null,

    @Column
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
)
