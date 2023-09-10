package com.gitub.oopgurus.refactoringproblems.configserver

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ConfigController(
    private val configSearchService: ConfigSearchService,
    private val configEditService: ConfigEditService,
) {

    @GetMapping(
        path = ["/v1/configs"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun getConfigs(): ResponseEntity<List<ConfigGetDto>> {
        configSearchService.getAllConfigs()
        return ResponseEntity.ok().build()
    }

    @PostMapping(
        path = ["/v1/configs/{configId}"],
        consumes = ["application/json"],
        produces = ["application/json"],
    )
    fun editConfigs(
        @RequestBody configEditDto: ConfigEditDto,
        @PathVariable configId: String,
    ): ResponseEntity<Any> {
        configEditService.editConfig(configId, configEditDto)
        return ResponseEntity.ok().build()
    }
}
