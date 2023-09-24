package com.gitub.oopgurus.refactoringproblems.configserver

interface WhatIWantToProperties {
    fun descriptions(descriptions: List<String>)

    fun properties(properties: Map<String, String>)
}