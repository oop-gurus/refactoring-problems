package com.gitub.oopgurus.refactoringproblems.configserver

interface WhatIWantToConfig {
    fun id(id: Long)
    fun properties(properties: Map<String, String>)
    fun descriptions(descriptions: List<String>)
}