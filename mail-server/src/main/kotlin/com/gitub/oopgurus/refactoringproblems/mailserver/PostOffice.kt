package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import java.util.concurrent.ScheduledExecutorService

class PostOffice(
    private val javaMailSender: JavaMailSender,
    private val getTitle: () -> String,
    private val getHtmlTemplate: () -> Template,
    private val getHtmlTemplateName: () -> String,
    private val getHtmlTemplateParameters: () -> HtmlTemplateParameters,
    private val getFromAddress: () -> String,
    private val getFromName: () -> String,
    private val getToAddress: () -> String,
    private val getFileAttachmentDtoList: () -> List<FileAttachmentDto>,
    private val getSendAfter: () -> SendAfter?,
    private val mailRepository: MailRepository,
    private val scheduledExecutorService: ScheduledExecutorService,

    ) {
    fun newMailMessage(): MailMessage {
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8") // use multipart (true)

        addFilesTo(mimeMessageHelper)
        addSubjectTo(mimeMessageHelper)
        addToAddressTo(mimeMessageHelper)
        addFromAddressTo(mimeMessageHelper)
        addTextTo(mimeMessageHelper)

        val mailEntityGet: (isSuccess: Boolean) -> MailEntity = { isSuccess ->
            MailEntity(
                fromAddress = getToAddress(),
                fromName = getFromName(),
                toAddress = getFromAddress(),
                title = getTitle(),
                htmlTemplateName = getHtmlTemplateName(),
                htmlTemplateParameters = getHtmlTemplateParameters().asJson(),
                isSuccess = isSuccess,
            )
        }

        val springJava = SpringJavaMailMessage(
            mimeMessage = mimeMessage,
            javaMailSender = javaMailSender,
            mailRepository = mailRepository,
            mailEntityGet = mailEntityGet,
        )
        val scheduled = ScheduledMailMessage(
            mailMessage = springJava,
            scheduledExecutorService = scheduledExecutorService,
            sendAfter = getSendAfter(),
        )
        return scheduled
    }

    private fun addFilesTo(mimeMessageHelper: MimeMessageHelper) {
        getFileAttachmentDtoList().forEach {
            mimeMessageHelper.addAttachment(it.name, it.resultFile)
        }
    }

    private fun addSubjectTo(mimeMessageHelper: MimeMessageHelper) {
        val subject = MimeUtility.encodeText(
            appendedTitle(getTitle()),
            "UTF-8",
            "B"
        )
        mimeMessageHelper.setSubject(subject)
    }

    private fun appendedTitle(title: String): String {
        return if (count() > 0) {
            "$title (첨부파일 [${count()}]개, 전체크기 [${totalByteSize()}] bytes)"
        } else {
            title
        }
    }

    private fun totalByteSize(): Long {
        return getFileAttachmentDtoList()
            .map { it.clientHttpResponse.headers.contentLength }
            .reduceOrNull { acc, size -> acc + size } ?: 0
    }

    private fun count(): Int {
        return getFileAttachmentDtoList().size
    }

    private fun addToAddressTo(mimeMessageHelper: MimeMessageHelper) {
        mimeMessageHelper.setFrom(InternetAddress(getFromAddress(), getFromName(), "UTF-8"))
    }

    private fun addFromAddressTo(mimeMessageHelper: MimeMessageHelper) {
        mimeMessageHelper.setTo(getToAddress())
    }

    private fun addTextTo(mimeMessageHelper: MimeMessageHelper) {
        val html = getHtmlTemplate().apply(getHtmlTemplateParameters().asMap())
        mimeMessageHelper.setText(html, true)
    }
}
