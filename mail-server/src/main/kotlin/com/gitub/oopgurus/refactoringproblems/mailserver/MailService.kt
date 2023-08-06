package com.gitub.oopgurus.refactoringproblems.mailserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Template
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeUtility
import mu.KotlinLogging
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


    fun send(sendMailDtos: List<SendMailDto>) {
        sendMailDtos.forEach {
            sendSingle(it)
        }
    }

    private fun sendSingle(sendMailDto: SendMailDto) {
        raiseIfBadRequest(sendMailDto)

        val template = findMailTemplate(sendMailDto.htmlTemplateName)
        val html = template.apply(sendMailDto.htmlTemplateParameters)
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()

        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, true, "UTF-8") // use multipart (true)
            mimeMessageHelper.setText(html, true)
            mimeMessageHelper.setFrom(InternetAddress(sendMailDto.fromAddress, sendMailDto.fromName, "UTF-8"))
            mimeMessageHelper.setTo(sendMailDto.toAddress)

            val postfixTitleByAttachmentFile = attachmentFile(sendMailDto.fileAttachments, mimeMessageHelper)
            mimeMessageHelper.setSubject(
                MimeUtility.encodeText(
                    sendMailDto.title + postfixTitleByAttachmentFile,
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
                                fromAddress = sendMailDto.fromAddress,
                                fromName = sendMailDto.fromName,
                                toAddress = sendMailDto.toAddress,
                                title = sendMailDto.title,
                                htmlTemplateName = sendMailDto.htmlTemplateName,
                                htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
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
                        fromAddress = sendMailDto.fromAddress,
                        fromName = sendMailDto.fromName,
                        toAddress = sendMailDto.toAddress,
                        title = sendMailDto.title,
                        htmlTemplateName = sendMailDto.htmlTemplateName,
                        htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                        isSuccess = true,
                    )
                )
                log.info { "MailServiceImpl.sendMail() :: SUCCESS" }
            }
        } catch (e: Exception) {
            mailRepository.save(
                MailEntity(
                    fromAddress = sendMailDto.fromAddress,
                    fromName = sendMailDto.fromName,
                    toAddress = sendMailDto.toAddress,
                    title = sendMailDto.title,
                    htmlTemplateName = sendMailDto.htmlTemplateName,
                    htmlTemplateParameters = objectMapper.writeValueAsString(sendMailDto.htmlTemplateParameters),
                    isSuccess = false,
                )
            )
            log.error(e) { "MailServiceImpl.sendMail() :: FAILED" }
        }
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

    private fun raiseIfBadRequest(sendMailDto: SendMailDto) {
        /** 사용자 Request 검증 -> Bad Request */
        Regex(".+@.*\\..+").matches(sendMailDto.toAddress).let {
            if (it.not()) {
                throw RuntimeException("이메일 형식 오류")
            }
        }
        Regex(".+@.*\\..+").matches(sendMailDto.fromAddress).let {
            if (it.not()) {
                throw RuntimeException("이메일 형식 오류")
            }
        }
        if (sendMailDto.title.isBlank()) {
            throw RuntimeException("제목이 비어있습니다")
        }
        if (sendMailDto.htmlTemplateName.isBlank()) {
            throw RuntimeException("템플릿 이름이 비어있습니다")
        }
        if (sendMailDto.fromName.isBlank()) {
            throw RuntimeException("발신자 이름이 비어있습니다")
        }


        /** 메일 차단 - 비지니스 영역 */
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
    }

    private fun findMailTemplate(templateName: String): Template {
        val htmlTemplate = mailTemplateRepository.findByName(templateName)
            ?: throw RuntimeException("템플릿이 존재하지 않습니다: [${templateName}]")

        return handlebars.compileInline(htmlTemplate.htmlBody)
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
