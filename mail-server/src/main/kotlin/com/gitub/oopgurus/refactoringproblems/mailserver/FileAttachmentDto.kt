package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.unit.DataSize
import java.io.File

data class FileAttachmentDto(
    val resultFile: File,
    val name: String,
    val clientHttpResponse: ClientHttpResponse,
) {
    init {
        if (resultFile.length() != clientHttpResponse.headers.contentLength) {
            throw RuntimeException("파일 크기 불일치")
        }
        if (DataSize.ofKilobytes(MAX_SIZE) <= DataSize.ofBytes(clientHttpResponse.headers.contentLength)) {
            throw RuntimeException("파일 크기 초과")
        }
    }

    companion object {
        val MAX_SIZE = 2048L
    }
}
