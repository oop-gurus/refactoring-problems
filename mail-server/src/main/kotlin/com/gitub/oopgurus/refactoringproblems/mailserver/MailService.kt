package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.seconds


@Component
class MailService(
    private val javaMailSender: JavaMailSender,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailRepository: MailRepository,
    private val objectMapper: ObjectMapper,
    private val mailMessageFactory: MailMessageFactory
) {

    fun send(sendMailDtos: List<SendMailDto>) {
        sendMailDtos.forEach {
            sendSingle(it)
        }
    }

    private fun sendSingle(sendMailDto: SendMailDto) {
        /** 발송할 메일 생성 */
        val mail = mailMessageFactory.makeSingle(sendMailDto)

        /** Client 생성 */
        var mailClient: MailClient = ImmediateMailClient(javaMailSender, mailRepository, mailResult(sendMailDto))
        if (sendMailDto.sendAfterSeconds != null) {
            mailClient = Reserve(
                waitTime = sendMailDto.sendAfterSeconds.seconds,
                decoratedMailClient = mailClient
            )
        }

        /** 발송 */
        mailClient.sendMail(mail)
    }

    private fun mailResult(sendMailDto: SendMailDto) = { isSuccess: Boolean ->
        MailEntity(
            fromAddress = sendMailDto.fromAddress,
            fromName = sendMailDto.fromName,
            toAddress = sendMailDto.toAddress,
            title = sendMailDto.title,
            htmlTemplateName = sendMailDto.htmlTemplateName,
            htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.fileAttachments),
            isSuccess = isSuccess,
        )
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
