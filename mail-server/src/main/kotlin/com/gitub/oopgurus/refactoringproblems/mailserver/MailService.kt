package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import mu.KotlinLogging
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


    // 객체를 일단 만들면 뭐든 할 수 있다
    //   - 메일 발송 실패할 수도 있는거 아님? -> 이건 예외가 아니라 비즈니스 실패이므로 객체생성 ok
    // from validation
    // to validation
    // 의존성 없이 validation 가능한거
    //   - 이메일 형식
    // http or db or 라이브러리 등 의존성이 필요한거
    //   - 수신거부 메일 도메인 목록
    //   - 메일 과금 회원 정보
    //   - 템플릿 변수 확인
    // 복잡한 로직이 있는 의존성이 필요한거
    //   - 메일 과금 정책 -> 현재 메일 크레딧이 얼마나 남아있는지 확인
    //   - 메일 API 초당 호출 횟수 제한
    // 예약 메일 기능
    //   - 일단 등록하고 나중에 발송 (성공 실패를 어떻게 알지?)

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

    class GetToAddressFactory(
        private val mailSpamService: MailSpamService,
        private val toAddress: String,
    ) {
        fun create(): () -> String {
            mailSpamService.needBlockByDomainName(toAddress).let {
                if (it) {
                    return { throw RuntimeException("도메인 차단") }
                }
            }
            mailSpamService.needBlockByRecentSuccess(toAddress).let {
                if (it) {
                    return { throw RuntimeException("최근 메일 발송 실패로 인한 차단") }
                }
            }
            Regex(".+@.*\\..+").matches(toAddress).let {
                if (it.not()) {
                    return { throw RuntimeException("이메일 형식 오류") }
                }
            }
            return { toAddress }
        }
    }

    class GetFromAddressFactory(
        private val fromAddress: String,
    ) {
        fun create(): () -> String {
            Regex(".+@.*\\..+").matches(fromAddress).let {
                if (it.not()) {
                    return { throw RuntimeException("이메일 형식 오류") }
                }
            }
            return { fromAddress }
        }
    }

    class GetHtmlTemplate(
        private val mailTemplateRepository: MailTemplateRepository,
        private val handlebars: Handlebars,
        private val getHtmlTemplateName: () -> String,
    ) {
        fun create(): () -> Template {
            val htmlTemplateName = getHtmlTemplateName()
            val htmlTemplate = mailTemplateRepository.findByName(htmlTemplateName)
            if (htmlTemplate == null) {
                return { throw RuntimeException("템플릿이 존재하지 않습니다: [$htmlTemplateName]") }
            }
            val template = handlebars.compileInline(htmlTemplate.htmlBody)
            return { template }
        }
    }

    class GetTitle(
        private val title: String,
    ) {
        fun create(): () -> String {
            if (title.isBlank()) {
                return { throw RuntimeException("제목이 비어있습니다") }
            }
            return { title }
        }
    }

    class GetFromName(
        private val fromName: String,
    ) {
        fun create(): () -> String {
            if (fromName.isBlank()) {
                return { throw RuntimeException("이름이 비어있습니다") }
            }
            return { fromName }
        }
    }

    class GetHtmlTemplateName(
        private val htmlTemplateName: String,
    ) {
        fun create(): () -> String {
            if (htmlTemplateName.isBlank()) {
                return { throw RuntimeException("템플릿 이름이 비어있습니다") }
            }
            return { htmlTemplateName }
        }
    }

    class GetHtmlTemplateParameters(
        private val htmlTemplateParameters: Map<String, Any>,
        private val objectMapper: ObjectMapper,
    ) {
        fun create(): () -> HtmlTemplateParameters {
            return {
                HtmlTemplateParameters(
                    holder = htmlTemplateParameters,
                    objectMapper = objectMapper,
                )
            }
        }
    }

    class HtmlTemplateParameters(
        private val holder: Map<String, Any>,
        private val objectMapper: ObjectMapper,
    ) {
        fun asMap(): Map<String, Any> {
            return HashMap(holder)
        }

        fun asJson(): String {
            return objectMapper.writeValueAsString(holder)
        }
    }

    class Attachments(
        private val fileAttachmentDtoList: List<FileAttachmentDto>,
    ) {
        fun count(): Int {
            return fileAttachmentDtoList.size
        }

        fun byteSize(): Long {
            return fileAttachmentDtoList
                .map { it.clientHttpResponse.headers.contentLength }
                .reduceOrNull { acc, size -> acc + size } ?: 0
        }

        fun applyTo(mimeMessageHelper: MimeMessageHelper) {
            fileAttachmentDtoList.forEach {
                mimeMessageHelper.addAttachment(it.name, it.resultFile)
            }
        }
    }

    class GetFileAttachments(
        private val fileAttachments: List<FileAttachment>,
        private val restTemplate: RestTemplate,
    ) {
        fun create(): () -> Attachments {
            val fileAttachmentDtoList = fileAttachments.mapIndexed { index, attachment ->
                val fileAttachmentDto = restTemplate.execute(
                    attachment.url,
                    HttpMethod.GET,
                    null,
                    { clientHttpResponse: ClientHttpResponse ->
                        val id = "file-${index}-${java.util.UUID.randomUUID()}"
                        val tempFile = File.createTempFile(id, "")
                        StreamUtils.copy(clientHttpResponse.body, FileOutputStream(tempFile))

                        FileAttachmentDto(
                            resultFile = tempFile,
                            name = attachment.name,
                            clientHttpResponse = clientHttpResponse
                        )
                    })

                if (fileAttachmentDto == null) {
                    throw RuntimeException("파일 초기화 실패")
                }
                if (fileAttachmentDto.resultFile.length() != fileAttachmentDto.clientHttpResponse.headers.contentLength) {
                    throw RuntimeException("파일 크기 불일치")
                }
                if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(fileAttachmentDto.clientHttpResponse.headers.contentLength)) {
                    throw RuntimeException("파일 크기 초과")
                }

                fileAttachmentDto
            }

            return { Attachments(fileAttachmentDtoList = fileAttachmentDtoList) }
        }
    }

    private fun sendSingle(sendMailDto: SendMailDto) {
        val getToAddress = GetToAddressFactory(
            mailSpamService = mailSpamService,
            toAddress = sendMailDto.toAddress,
        ).create()

        val getFromAddress = GetFromAddressFactory(
            fromAddress = sendMailDto.fromAddress,
        ).create()

        val getHtmlTemplateName = GetHtmlTemplateName(
            htmlTemplateName = sendMailDto.htmlTemplateName,
        ).create()

        val getHtmlTemplate = GetHtmlTemplate(
            mailTemplateRepository = mailTemplateRepository,
            handlebars = handlebars,
            getHtmlTemplateName = getHtmlTemplateName,
        ).create()

        val getTitle = GetTitle(
            title = sendMailDto.title,
        ).create()

        val getFromName = GetFromName(
            fromName = sendMailDto.fromName,
        ).create()

        val getHtmlTemplateParameters = GetHtmlTemplateParameters(
            htmlTemplateParameters = sendMailDto.htmlTemplateParameters,
            objectMapper = objectMapper,
        ).create()

        val getAttachments = GetFileAttachments(
            fileAttachments = sendMailDto.fileAttachments,
            restTemplate = restTemplate,
        ).create()

        val html = getHtmlTemplate().apply(getHtmlTemplateParameters().asMap())
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()

        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8") // use multipart (true)
            mimeMessageHelper.setText(html, true)
            mimeMessageHelper.setFrom(InternetAddress(getFromAddress(), getFromName(), "UTF-8"))
            mimeMessageHelper.setTo(getToAddress())

            val attachments = getAttachments()


            attachments.applyTo(mimeMessageHelper)


            var postfixTitle = ""
            if (getAttachments().count() > 0) {
                val totalSize = getAttachments().byteSize()
                postfixTitle = " (첨부파일 [${getAttachments().count()}]개, 전체크기 [$totalSize bytes])"
            }
            mimeMessageHelper.setSubject(
                MimeUtility.encodeText(
                    getTitle() + postfixTitle,
                    "UTF-8",
                    "B"
                )
            ) // Base64 encoding


            if (sendMailDto.sendAfterSeconds != null) {
                scheduledExecutorService.schedule(
                    {
                        javaMailSender.send(mimeMessage)
                        mailRepository.save(
                            MailEntity(
                                fromAddress = getFromAddress(),
                                fromName = getFromName(),
                                toAddress = getToAddress(),
                                title = getTitle(),
                                htmlTemplateName = getHtmlTemplateName(),
                                htmlTemplateParameters = getHtmlTemplateParameters().asJson(),
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
                        fromAddress = getFromAddress(),
                        fromName = getFromName(),
                        toAddress = getToAddress(),
                        title = getTitle(),
                        htmlTemplateName = getHtmlTemplateName(),
                        htmlTemplateParameters = getHtmlTemplateParameters().asJson(),
                        isSuccess = true,
                    )
                )
                log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
            }
        } catch (e: Exception) {
            mailRepository.save(
                MailEntity(
                    fromAddress = getFromAddress(),
                    fromName = getFromName(),
                    toAddress = getToAddress(),
                    title = getTitle(),
                    htmlTemplateName = getHtmlTemplateName(),
                    htmlTemplateParameters = getHtmlTemplateParameters().asJson(),
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
