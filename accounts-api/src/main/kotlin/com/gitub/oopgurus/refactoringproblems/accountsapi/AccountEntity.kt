package com.gitub.oopgurus.refactoringproblems.accountsapi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "is_verified", updatable = true)
    var isVerified: Boolean,

    @Column(name = "is_closed", updatable = true)
    var isClosed: Boolean,

    @Column(name = "is_frozen", updatable = true)
    var isFrozen: Boolean,

    @Column(name = "balance", updatable = true, precision = 15, scale = 2)
    var balance: BigDecimal,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
