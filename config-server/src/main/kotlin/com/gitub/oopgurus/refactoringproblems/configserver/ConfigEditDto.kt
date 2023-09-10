package com.gitub.oopgurus.refactoringproblems.configserver

data class ConfigEditDto(
    val systemOn: Boolean?,
    val systemNotes: String?,
    val properties: Map<String, String>?,
    val personPhone: String?,
    val personFirstName: String?,
    val personLastName: String?,
)
