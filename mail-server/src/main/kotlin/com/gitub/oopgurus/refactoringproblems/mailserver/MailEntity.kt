package com.gitub.oopgurus.refactoringproblems.mailserver

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
class MailEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title", updatable = false)
    var title: String,

    @Column(name = "from_address", updatable = false)
    var fromAddress: String,

    @Column(name = "from_name", updatable = false)
    var fromName: String,

    @Column(name = "to_address", updatable = false)
    var toAddress: String,

    @Column(name = "html_template_name", updatable = false)
    var htmlTemplateName: String,

    @Lob
    @Column(name = "html_template_parameters", updatable = false)
    var htmlTemplateParameters: String,

    @Column(name = "is_success", updatable = false)
    var isSuccess: Boolean,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
