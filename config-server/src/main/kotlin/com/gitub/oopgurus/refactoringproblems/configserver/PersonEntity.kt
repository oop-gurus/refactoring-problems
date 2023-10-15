package com.gitub.oopgurus.refactoringproblems.configserver

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
class PersonEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val configId: Long,

    @Column
    var firstName: String?,

    @Column
    var lastName: String?,

    @Column
    var email: String?,

    @Column
    var phone: String?,

    @Column
    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null,

    @Column
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
)
