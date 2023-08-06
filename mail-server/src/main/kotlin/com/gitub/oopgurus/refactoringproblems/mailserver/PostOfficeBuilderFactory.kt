package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PostOfficeBuilderFactory(
    private val mailSpamService: MailSpamService,
    private val mailTemplateRepository: MailTemplateRepository,
    private val handlebars: Handlebars,
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate,
    private val javaMailSender: JavaMailSender,
    ) {

    fun create(): PostOfficeBuilder {
        return PostOfficeBuilder(
            mailSpamService = mailSpamService,
            mailTemplateRepository = mailTemplateRepository,
            handlebars = handlebars,
            objectMapper = objectMapper,
            restTemplate = restTemplate,
            javaMailSender = javaMailSender,
        )
    }
}
