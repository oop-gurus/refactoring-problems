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
    private val mailMessageBuilderFactory: MailMessageBuilderFactory,
) {

    @PostMapping(
        path = ["/v1/mails"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun sendMail(@RequestBody sendMailDtoList: List<SendMailDto>): ResponseEntity<Any> {
        val mailMessage = mailMessageBuilderFactory.bulk()
            .singleBuilder { singleBuilder(it) }
            .buildWith(sendMailDtoList)

        mailMessage.send()
            .register()

        return ResponseEntity.ok().build()
    }

    private fun singleBuilder(it: SendMailDto) =
        mailMessageBuilderFactory.single()
            .toAddress(it.toAddress)
            .fromName(it.fromName)
            .fromAddress(it.fromAddress)
            .title(it.title)
            .htmlTemplateName(it.htmlTemplateName)
            .htmlTemplateParameters(it.htmlTemplateParameters)
            .fileAttachments(it.fileAttachments)
            .sendAfterSeconds(it.sendAfterSeconds)
            .build()


    @PostMapping(
        path = ["/v1/mail-templates"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun createMailTemplate(@RequestBody createMailTemplateDtos: List<CreateMailTemplateDto>): ResponseEntity<Any> {
        mailService.creatMailTemplate(createMailTemplateDtos)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/accounts/{accountId}")
    fun getMail(
        @PathVariable accountId: Long,
    ): ResponseEntity<GetMailDto> {
        TODO()
    }
}
