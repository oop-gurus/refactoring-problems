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
    private val titleSupplier: () -> String,
    private val htmlTemplateSupplier: () -> Template,
    private val htmlTemplateNameSupplier: () -> String,
    private val fromAddressSupplier: () -> String,
    private val fromNameSupplier: () -> String,
    private val toAddressSupplier: () -> String,
    private val htmlTemplateParameters: HtmlTemplateParameters,
    private val fileAttachmentDtoListSupplier: () -> List<FileAttachmentDto>,
    private val sendAfterSupplier: () -> SendAfter?,
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

        val springJavaMailMessage = SpringJavaMailMessage(
            mimeMessage = mimeMessage,
            htmlTemplateName = htmlTemplateNameSupplier(),
            htmlTemplateParameters = htmlTemplateParameters,
            title = titleSupplier(),
            fromAddress = fromAddressSupplier(),
            fromName = fromNameSupplier(),
            toAddress = toAddressSupplier(),
            javaMailSender = javaMailSender,
            mailRepository = mailRepository,
        )

        val scheduled = ScheduledMailMessage(
            mailMessage = springJavaMailMessage,
            scheduledExecutorService = scheduledExecutorService,
            sendAfter = sendAfterSupplier(),
        )

        return scheduled
    }

    private fun addFilesTo(mimeMessageHelper: MimeMessageHelper) {
        fileAttachmentDtoListSupplier().forEach {
            mimeMessageHelper.addAttachment(it.name, it.resultFile)
        }
    }

    private fun addSubjectTo(mimeMessageHelper: MimeMessageHelper) {
        val subject = MimeUtility.encodeText(
            appendedTitle(titleSupplier()),
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
        return fileAttachmentDtoListSupplier()
            .map { it.clientHttpResponse.headers.contentLength }
            .reduceOrNull { acc, size -> acc + size } ?: 0
    }

    private fun count(): Int {
        return fileAttachmentDtoListSupplier().size
    }

    private fun addToAddressTo(mimeMessageHelper: MimeMessageHelper) {
        mimeMessageHelper.setFrom(InternetAddress(fromAddressSupplier(), fromNameSupplier(), "UTF-8"))
    }

    private fun addFromAddressTo(mimeMessageHelper: MimeMessageHelper) {
        mimeMessageHelper.setTo(toAddressSupplier())
    }

    private fun addTextTo(mimeMessageHelper: MimeMessageHelper) {
        val html = htmlTemplateSupplier().apply(htmlTemplateParameters.asMap())
        mimeMessageHelper.setText(html, true)
    }
}