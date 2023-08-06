package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MailController(
        private val mailService: MailService,
        private val mailTemplateCreator: MailTemplateCreator
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
    fun createMailTemplate(@RequestBody jsonCreateMailTemplateRequests: List<JsonCreateMailTemplateRequest>): ResponseEntity<Any> {
        mailTemplateCreator.create(jsonCreateMailTemplateRequests.map { it.toCreateMailTemplateDto() })
        return ResponseEntity.ok().build()
    }
}
