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
        val mailMessageList = sendMailDtoList.map { sendMailDto ->
            mailMessageBuilderFactory.create()
                .toAddress(sendMailDto.toAddress)
                .fromName(sendMailDto.fromName)
                .fromAddress(sendMailDto.fromAddress)
                .title(sendMailDto.title)
                .htmlTemplateName(sendMailDto.htmlTemplateName)
                .htmlTemplateParameters(sendMailDto.htmlTemplateParameters)
                .fileAttachments(sendMailDto.fileAttachments)
                .sendAfterSeconds(sendMailDto.sendAfterSeconds)
                .build()
        }

        mailMessageList.forEach { mailMessage ->
            mailMessage.send()
                .register()
        }
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

    @GetMapping("/v1/accounts/{accountId}")
    fun getMail(
        @PathVariable accountId: Long,
    ): ResponseEntity<GetMailDto> {
        TODO()
    }
}
