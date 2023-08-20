package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import org.springframework.util.unit.DataSize
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream

class FileAttachmentDtoListSupplierFactory(
    private val restTemplate: RestTemplate,
    private val fileAttachments: List<FileAttachment>,
) {
    fun create(): () -> List<FileAttachmentDto> {
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
                }) ?: throw RuntimeException("파일 초기화 실패")

            if (fileAttachmentDto.resultFile.length() != fileAttachmentDto.clientHttpResponse.headers.contentLength) {
                throw RuntimeException("파일 크기 불일치")
            }

            if (DataSize.ofKilobytes(2048) <= DataSize.ofBytes(fileAttachmentDto.clientHttpResponse.headers.contentLength)) {
                throw RuntimeException("파일 크기 초과")
            }

            fileAttachmentDto
        }

        return { fileAttachmentDtoList }
    }
}