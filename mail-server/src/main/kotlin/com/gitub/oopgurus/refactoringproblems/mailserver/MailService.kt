package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.stereotype.Component

@Component
class MailService(
    private val mailTemplateRepository: MailTemplateRepository,
) {
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
