package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper

class HtmlTemplateParameters(
    private val parameters: Map<String, Any>,
    private val objectMapper: ObjectMapper,
) {
    fun asMap(): Map<String, Any> {
        return HashMap(parameters)
    }

    fun asJson(): String {
        return objectMapper.writeValueAsString(parameters)
    }
}