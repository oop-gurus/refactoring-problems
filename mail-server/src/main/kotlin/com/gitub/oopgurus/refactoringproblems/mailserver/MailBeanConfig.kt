package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
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
    fun handlebars(): Handlebars {
        return Handlebars().also {
            it.registerHelperMissing(Helper<Any> { context, options ->
                throw IllegalArgumentException("누락된 파라메터 발생: [${options.helperName}]")
            })
        }
    }
}
