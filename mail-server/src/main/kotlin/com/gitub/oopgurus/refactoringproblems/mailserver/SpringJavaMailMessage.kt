package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender

class SpringJavaMailMessage(
    private val mimeMessage: MimeMessage,
    private val htmlTemplateName: String,
    private val htmlTemplateParameters: HtmlTemplateParameters,
    private val title: String,
    private val fromAddress: String,
    private val fromName: String,
    private val toAddress: String,
    private val javaMailSender: JavaMailSender,
    private val mailRepository: MailRepository,
) : MailMessage {

    private val mailEntityGet: (isSuccess: Boolean) -> MailEntity = { isSuccess ->
        MailEntity(
            fromAddress = fromAddress,
            fromName = fromName,
            toAddress = toAddress,
            title = title,
            htmlTemplateName = htmlTemplateName,
            htmlTemplateParameters = htmlTemplateParameters.asJson(),
            isSuccess = isSuccess,
        )
    }

    override fun send(): MailSendResult {
        return try {
            javaMailSender.send(mimeMessage)

            MailSendResultSuccess(
                mailRepository = mailRepository,
                mailEntityGet = mailEntityGet,
            )
        } catch (exception: Exception) {
            MailSendResultFailed(
                mailRepository = mailRepository,
                mailEntityGet = mailEntityGet,
                exception = exception,
            )
        }
    }
}
