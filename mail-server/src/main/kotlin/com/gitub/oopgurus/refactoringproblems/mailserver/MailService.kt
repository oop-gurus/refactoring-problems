package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.stereotype.Component


@Component
class MailService(
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailMessageBuilderFactory: MailMessageBuilderFactory,
) {
    fun send(sendMailDtos: List<SendMailDto>) {
        sendMailDtos.forEach {
            sendSingle(it)
        }
    }

    private fun sendSingle(sendMailDto: SendMailDto) {
        val mailMessage = mailMessageBuilderFactory.create()
            .toAddress(sendMailDto.toAddress)
            .fromName(sendMailDto.fromName)
            .fromAddress(sendMailDto.fromAddress)
            .title(sendMailDto.title)
            .htmlTemplateName(sendMailDto.htmlTemplateName)
            .htmlTemplateParameters(sendMailDto.htmlTemplateParameters)
            .fileAttachments(sendMailDto.fileAttachments)
            .sendAfterSeconds(sendMailDto.sendAfterSeconds)
            .build()

        mailMessage.send()
            .register()
    }

    fun creatMailTemplate(createMailTemplateDtos: List<CreateMailTemplateDto>) {
        createMailTemplateDtos.forEach {
            if (it.htmlBody.isBlank()) {
                throw IllegalArgumentException("htmlBody is blank")
            }
            mailTemplateRepository.save(
                MailTemplateEntity(
                    name = it.name,
                    htmlBody = it.htmlBody,
                )
            )
        }
    }
}


