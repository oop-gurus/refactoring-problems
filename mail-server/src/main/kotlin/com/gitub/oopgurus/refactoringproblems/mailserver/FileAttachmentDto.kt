package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.http.client.ClientHttpResponse
import java.io.File

data class FileAttachmentDto(
    val resultFile: File,
    val name: String,
    val clientHttpResponse: ClientHttpResponse,
)
