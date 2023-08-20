package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import mu.KotlinLogging
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Component
class MailService(
    private val javaMailSender: JavaMailSender,
    private val restTemplate: RestTemplate,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailRepository: MailRepository,
    private val objectMapper: ObjectMapper,
    private val mailSpamService: MailSpamService,
) {

    private val log = KotlinLogging.logger {}
    private val handlebars = Handlebars().also {
        it.registerHelperMissing(Helper<Any> { context, options ->
            throw IllegalArgumentException("누락된 파라메터 발생: [${options.helperName}]")
        })
    }
    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    data class FileAttachmentDto(
        val resultFile: File,
        val name: String,
        val clientHttpResponse: ClientHttpResponse,
    )

    fun send(sendMailDtos: List<SendMailDto>) {
        sendMailDtos.forEach {
            sendSingle(it)
        }
    }

    private fun sendSingle(sendMailDto: SendMailDto) {
        val toAddressSupplier = ToAddressSupplierFactory(
            mailSpamService = mailSpamService,
            toAddress = sendMailDto.toAddress,
        ).create()

        val fromAddressSupplier = FromAddressSupplierFactory(
            fromAddress = sendMailDto.fromAddress,
        ).create()

        val titleSupplier = TitleSupplierFactory(
            title = sendMailDto.title,
        ).create()

        val htmlTemplateNameSupplier = HtmlTemplateNameSupplierFactory(
            htmlTemplateName = sendMailDto.htmlTemplateName,
        ).create()

        val fromNameSupplier = FromNameSupplierFactory(
            fromName = sendMailDto.fromName,
        ).create()

        val htmlTemplateSupplier = HtmlTemplateSupplierFactory(
            mailTemplateRepository = mailTemplateRepository,
            htmlTemplateNameSupplier = htmlTemplateNameSupplier,
            handlebars = handlebars,
        ).create()

        val htmlTemplateParameters = HtmlTemplateParameters(
            parameters = sendMailDto.htmlTemplateParameters,
            objectMapper = objectMapper,
        )

        val attachmentsSupplier = MimeMessageFactorySupplierFactory(
            javaMailSender = javaMailSender,
            htmlTemplateParameters = htmlTemplateParameters,
            fileAttachments = sendMailDto.fileAttachments,
            restTemplate = restTemplate,
            titleSupplier = titleSupplier,
            htmlTemplateSupplier = htmlTemplateSupplier,
            fromAddressSupplier = fromAddressSupplier,
            fromNameSupplier = fromNameSupplier,
            toAddressSupplier = toAddressSupplier,
        ).create()

        try {
            val attachments = attachmentsSupplier()
            val mimeMessage = attachments.create()

            if (sendMailDto.sendAfterSeconds != null) {
                scheduledExecutorService.schedule(
                    {
                        javaMailSender.send(mimeMessage)
                        mailRepository.save(
                            MailEntity(
                                fromAddress = fromAddressSupplier(),
                                fromName = fromNameSupplier(),
                                toAddress = toAddressSupplier(),
                                title = titleSupplier(),
                                htmlTemplateName = htmlTemplateNameSupplier(),
                                htmlTemplateParameters = htmlTemplateParameters.asJson(),
                                isSuccess = true,
                            )
                        )
                        log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
                    },
                    sendMailDto.sendAfterSeconds,
                    TimeUnit.SECONDS
                )

            } else {
                javaMailSender.send(mimeMessage)
                mailRepository.save(
                    MailEntity(
                        fromAddress = fromAddressSupplier(),
                        fromName = fromNameSupplier(),
                        toAddress = toAddressSupplier(),
                        title = titleSupplier(),
                        htmlTemplateName = htmlTemplateNameSupplier(),
                        htmlTemplateParameters = htmlTemplateParameters.asJson(),
                        isSuccess = true,
                    )
                )
                log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
            }
        } catch (e: Exception) {
            mailRepository.save(
                MailEntity(
                    fromAddress = fromAddressSupplier(),
                    fromName = fromNameSupplier(),
                    toAddress = toAddressSupplier(),
                    title = titleSupplier(),
                    htmlTemplateName = htmlTemplateNameSupplier(),
                    htmlTemplateParameters = htmlTemplateParameters.asJson(),
                    isSuccess = false,
                )
            )
            log.error(e) { "MailServiceImpl.sendMail() :: FAILED" }
        }
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
