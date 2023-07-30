package com.gitub.oopgurus.refactoringproblems.mailserver

data class GetMailDto(
    val id: String,
    val from: String,
    val to: String,
    val subject: String,
    val body: String,
    val sent: Boolean
)
