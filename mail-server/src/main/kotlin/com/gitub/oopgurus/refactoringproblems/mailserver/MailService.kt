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

        // 여기 아래부터는 "동작"을 나타냄
        // 어떤 동작들이 있는지 구분해보자면? (비개발자에게 설명한다고 하면 어떻게 말하겠는가?)
        // 1. 메일을 보낸다.
        //   - 바로 보낸다
        //   - 몇 초 뒤에 보낸다
        // 2. 그 결과를 저장한다
        //   - 발송이 성공한 경우
        //   - 발송이 실패한 경우 (실패한 이유도 같이)
        //   - db 에 / 로그에 각각
        //
        //
        // 여기서 동작과 그 동작의 세부사항을 구분할 수 있겠는가?
        // 동작1: 메일을 보낸다
        // 세부사항: 바로 보낸다 / 몇 초 뒤에 보낸다
        // 동작2: 그 결과를 저장한다
        // 세부사항: 발송이 성공한 경우 / 발송이 실패한 경우 (실패한 이유도 같이)
        // 세부사항: db에 저장 / 로그에 저장
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

