package com.gitub.oopgurus.refactoringproblems.mailserver

class MailTemplateCreator(
        private val mailTemplateRepository: MailTemplateRepository,
) {
    fun create(createMailTemplateDtoList: List<CreateMailTemplateDto>) {
        createMailTemplateDtoList.forEach {
            mailTemplateRepository.save(
                    MailTemplateEntity(
                            name = it.name,
                            htmlBody = it.htmlBody.contents,
                    )
            )
        }
    }
}