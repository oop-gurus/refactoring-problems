package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.concurrent.Executors

@Component
class MailMessageBuilderFactory(
    private val mailSpamService: MailSpamService,
    private val mailTemplateRepository: MailTemplateRepository,
    private val handlebars: Handlebars,
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate,
    private val javaMailSender: JavaMailSender,
    private val mailRepository: MailRepository,
) {
    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    fun single(): MailMessageBuilder {
        return MailMessageBuilder(
            mailSpamService = mailSpamService,
            mailTemplateRepository = mailTemplateRepository,
            handlebars = handlebars,
            objectMapper = objectMapper,
            restTemplate = restTemplate,
            javaMailSender = javaMailSender,
            mailRepository = mailRepository,
            scheduledExecutorService = scheduledExecutorService,
        )
    }

    fun bulk(): MailMessageBulkBuilder {
        return MailMessageBulkBuilder()
    }

    class MailMessageBulkBuilder {
        private var singleBuilder: (SendMailDto) -> MailMessage = { throw IllegalStateException("not initialized") }

        fun singleBuilder(singleBuilder: (SendMailDto) -> MailMessage): MailMessageBulkBuilder {
            this.singleBuilder = singleBuilder
            return this
        }

        fun buildWith(sendMailDtoList: List<SendMailDto>): MailMessage {
            return BulkMailMessage(sendMailDtoList.map(singleBuilder))
        }
    }
}