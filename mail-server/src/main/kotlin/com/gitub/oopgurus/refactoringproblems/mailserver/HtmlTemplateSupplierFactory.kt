package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template

class HtmlTemplateSupplierFactory(
    private val mailTemplateRepository: MailTemplateRepository,
    private val htmlTemplateNameSupplier: () -> String,
    private val handlebars: Handlebars,
) {
    fun create(): () -> Template {
        val htmlTemplateName = htmlTemplateNameSupplier()
        val htmlTemplate = mailTemplateRepository.findByName(htmlTemplateNameSupplier())
            ?: throw RuntimeException("템플릿이 존재하지 않습니다: [${htmlTemplateNameSupplier()}]")
        val template: Template = handlebars.compileInline(htmlTemplate.htmlBody)

        return { template }
    }
}