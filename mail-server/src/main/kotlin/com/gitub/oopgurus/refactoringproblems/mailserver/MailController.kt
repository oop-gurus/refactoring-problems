package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MailController(
    private val mailService: MailService,
) {
    @PostMapping(
        path = ["/v1/mails"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun sendMail(@RequestBody sendMailDto: List<SendMailDto>): ResponseEntity<Any> {
        mailService.send(sendMailDto)
        return ResponseEntity.ok().build()
    }


    @PostMapping(
        path = ["/v1/mail-templates"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun createMailTemplate(@RequestBody createMailTemplateDtos: List<CreateMailTemplateDto>): ResponseEntity<Any> {
        mailService.creatMailTemplate(createMailTemplateDtos)
        return ResponseEntity.ok().build()
    }
}
