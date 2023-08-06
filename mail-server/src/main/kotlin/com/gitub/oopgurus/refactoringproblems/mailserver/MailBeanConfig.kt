package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class MailBeanConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun mailTemplateCreateor(mailTemplateRepository: MailTemplateRepository) = MailTemplateCreator(mailTemplateRepository)
}
