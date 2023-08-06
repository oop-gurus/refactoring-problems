package com.gitub.oopgurus.refactoringproblems.mailserver

import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Component
class MailService(
    private val javaMailSender: JavaMailSender,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailRepository: MailRepository,
    private val postOfficeBuilderFactory: PostOfficeBuilderFactory,
) {

    private val log = KotlinLogging.logger {}
    private val scheduledExecutorService = Executors.newScheduledThreadPool(10)

    fun send(sendMailDtos: List<SendMailDto>) {
        sendMailDtos.forEach {
            sendSingle(it)
        }
    }

    private fun sendSingle(sendMailDto: SendMailDto) {
        val postOffice = postOfficeBuilderFactory.create()
            .toAddress(sendMailDto.toAddress)
            .fromName(sendMailDto.fromName)
            .fromAddress(sendMailDto.fromAddress)
            .title(sendMailDto.title)
            .htmlTemplateName(sendMailDto.htmlTemplateName)
            .htmlTemplateParameters(sendMailDto.htmlTemplateParameters)
            .fileAttachments(sendMailDto.fileAttachments)
            .build()

        val mailMessage = postOffice.newMailMessage()
        try {
            if (sendMailDto.sendAfterSeconds != null) {
                scheduledExecutorService.schedule(
                    {
                        javaMailSender.send(mailMessage.mimeMessage())
                        mailRepository.save(
                            MailEntity(
                                fromAddress = mailMessage.fromAddress(),
                                fromName = mailMessage.fromName(),
                                toAddress = mailMessage.toAddress(),
                                title = mailMessage.title(),
                                htmlTemplateName = mailMessage.htmlTemplateName(),
                                htmlTemplateParameters = mailMessage.htmlTemplateParameters().asJson(),
                                isSuccess = true,
                            )
                        )
                        log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
                    },
                    sendMailDto.sendAfterSeconds,
                    TimeUnit.SECONDS
                )

            } else {
                javaMailSender.send(mailMessage.mimeMessage())
                mailRepository.save(
                    MailEntity(
                        fromAddress = mailMessage.fromAddress(),
                        fromName = mailMessage.fromName(),
                        toAddress = mailMessage.toAddress(),
                        title = mailMessage.title(),
                        htmlTemplateName = mailMessage.htmlTemplateName(),
                        htmlTemplateParameters = mailMessage.htmlTemplateParameters().asJson(),
                        isSuccess = true,
                    )
                )
                log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
            }
        } catch (e: Exception) {
            mailRepository.save(
                MailEntity(
                    fromAddress = mailMessage.fromAddress(),
                    fromName = mailMessage.fromName(),
                    toAddress = mailMessage.toAddress(),
                    title = mailMessage.title(),
                    htmlTemplateName = mailMessage.htmlTemplateName(),
                    htmlTemplateParameters = mailMessage.htmlTemplateParameters().asJson(),
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

