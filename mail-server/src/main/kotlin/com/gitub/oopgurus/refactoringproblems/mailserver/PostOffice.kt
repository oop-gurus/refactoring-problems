package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper

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
) {
    fun newMailMessage(): MailMessage {
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8") // use multipart (true)

        addFilesTo(mimeMessageHelper)
        addSubjectTo(mimeMessageHelper)
        addToAddressTo(mimeMessageHelper)
        addFromAddressTo(mimeMessageHelper)
        addTextTo(mimeMessageHelper)

        return MailMessage(
            mimeMessage = mimeMessage,
            htmlTemplateName = getHtmlTemplateName(),
            htmlTemplateParameters = getHtmlTemplateParameters(),
            title = getTitle(),
            fromAddress = getFromAddress(),
            fromName = getFromName(),
            toAddress = getToAddress(),
        )
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
