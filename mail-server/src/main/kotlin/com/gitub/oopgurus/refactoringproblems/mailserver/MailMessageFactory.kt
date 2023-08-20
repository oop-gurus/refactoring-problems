package com.gitub.oopgurus.refactoringproblems.mailserver

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Component
class MailMessageFactory(
    private val javaMailSender: JavaMailSender,
    private val mailSpamService: MailSpamService,
    private val mailTemplateRepository: MailTemplateRepository,
    private val restTemplate: RestTemplate,
) {

    private val handlebars = Handlebars().also {
        it.registerHelperMissing(Helper<Any> { context, options ->
            throw IllegalArgumentException("누락된 파라메터 발생: [${options.helperName}]")
        })
    }

    /**
     * 생각 1. Builder 패턴의 장점은 MailMessageFactory 에서 sendMailDto를 의존 하지 않아도 되는 것
     *  -> 어떤 외부 클라이언트에서도 사용 가능하다. / 하지만 지금 client가 하나뿐인 상황에서 오버 엔지니어링 아닐까?
     *
     *  생각 2. 오히려 검증에 여러개의 필드가 필요하게 될 경우, Supplier 구조를 사용하기 어렵지 않을까
     */
    fun makeSingle(sendMailDto: SendMailDto): MimeMessage {
        val validatedMail = MailValidator(sendMailDto, mailSpamService)

        val mailTemplate = findMailTemplate(validatedMail.htmlTemplateName()())

        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
        val html = mailTemplate.apply(validatedMail.htmlTemplateParameters()())

        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8") // use multipart (true)
        mimeMessageHelper.setText(html, true)
        mimeMessageHelper.setFrom(InternetAddress(validatedMail.fromAddress()(), validatedMail.fromName()(), "UTF-8"))
        mimeMessageHelper.setTo(validatedMail.toAddress()())

        val postfixTitleByAttachmentFile = attachmentFile(sendMailDto.fileAttachments, mimeMessageHelper)
        mimeMessageHelper.setSubject(
            MimeUtility.encodeText(
                sendMailDto.title + postfixTitleByAttachmentFile,
                "UTF-8",
                "B"
            )
        ) // Base64 encoding

        return mimeMessage

    }


    private fun findMailTemplate(templateName: String): Template {
        val htmlTemplate = mailTemplateRepository.findByName(templateName)
            ?: throw RuntimeException("템플릿이 존재하지 않습니다: [${templateName}]")

        return handlebars.compileInline(htmlTemplate.htmlBody)
    }

    private fun attachmentFile(
        fileAttachments: List<FileAttachment>,
        mimeMessageHelper: MimeMessageHelper
    ): String? {

        data class FileAttachmentDto(
            val resultFile: File,
            val name: String,
            val clientHttpResponse: ClientHttpResponse,
        )

        val fileResults = fileAttachments.mapIndexed { index, attachment ->
            val result = restTemplate.execute(
                attachment.url,
                HttpMethod.GET,
                null,
                { clientHttpResponse: ClientHttpResponse ->
                    val id = "file-${index}-${UUID.randomUUID()}"
                    val tempFile = File.createTempFile(id, "")
                    StreamUtils.copy(clientHttpResponse.body, FileOutputStream(tempFile))

                    FileAttachmentDto(
                        resultFile = tempFile,
                        name = attachment.name,
                        clientHttpResponse = clientHttpResponse
                    )
                })

            if (result == null) {
                throw RuntimeException("파일 초기화 실패")
            }
            if (result.resultFile.length() != result.clientHttpResponse.headers.contentLength) {
                throw RuntimeException("파일 크기 불일치")
            }
            if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(result.clientHttpResponse.headers.contentLength)) {
                throw RuntimeException("파일 크기 초과")
            }
            result
        }

        fileResults.forEach {
            val fileSystemResource: FileSystemResource = FileSystemResource(File(it.resultFile.absolutePath))
            mimeMessageHelper.addAttachment(
                MimeUtility.encodeText(
                    it.name,
                    "UTF-8",
                    "B"
                ), fileSystemResource
            )
        }

        return if (fileResults.isNotEmpty()) {
            val totalSize = fileResults
                .map { it.clientHttpResponse.headers.contentLength }
                .reduceOrNull { acc, size -> acc + size } ?: 0
            " (첨부파일 [${fileResults.size}]개, 전체크기 [$totalSize bytes])"
        } else null
    }


    fun getTitle(toAddress: String): () -> String {
        Regex(".+@.*\\..+").matches(toAddress).let {
            if (it.not()) {
                throw RuntimeException("이메일 형식 오류")
            }
        }

        return { toAddress }
    }


}

class MailValidator(
    private val sendMailDto: SendMailDto,
    private val mailSpamService: MailSpamService,
) {
    fun toAddress(): () -> String {
        Regex(".+@.*\\..+").matches(sendMailDto.toAddress).let {
            if (it.not()) {
                throw RuntimeException("이메일 형식 오류")
            }
        }


        mailSpamService.needBlockByDomainName(sendMailDto.toAddress).let {
            if (it) {
                throw RuntimeException("도메인 차단")
            }
        }
        mailSpamService.needBlockByRecentSuccess(sendMailDto.toAddress).let {
            if (it) {
                throw RuntimeException("최근 메일 발송 실패로 인한 차단")
            }
        }


        return { sendMailDto.toAddress }
    }

    fun fromAddress(): () -> String {
        Regex(".+@.*\\..+").matches(sendMailDto.fromAddress).let {
            if (it.not()) {
                throw RuntimeException("이메일 형식 오류")
            }
        }

        return { sendMailDto.fromAddress }
    }

    fun title(): () -> String {
        if (sendMailDto.title.isBlank()) {
            throw RuntimeException("제목이 비어있습니다")
        }

        return { sendMailDto.title }
    }

    fun htmlTemplateName(): () -> String {
        if (sendMailDto.htmlTemplateName.isBlank()) {
            throw RuntimeException("템플릿 이름이 비어있습니다")
        }

        return { sendMailDto.htmlTemplateName }
    }

    fun fromName(): () -> String {
        if (sendMailDto.fromName.isBlank()) {
            throw RuntimeException("발신자 이름이 비어있습니다")
        }

        return { sendMailDto.fromName }
    }

    fun htmlTemplateParameters(): () -> Map<String, Any> {
        return { sendMailDto.htmlTemplateParameters }
    }

}