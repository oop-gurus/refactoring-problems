package com.gitub.oopgurus.refactoringproblems.mailserver

import jakarta.mail.internet.MimeMessage
import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

interface MailClient {
    fun sendMail(mimeMessage: MimeMessage)
}

class ImmediateMailClient(
    private val javaMailSender: JavaMailSender,
    private val mailRepository: MailRepository,
    private val mailResult: (isSuccess: Boolean) -> MailEntity
) : MailClient {

    private val log = KotlinLogging.logger {}


    override fun sendMail(mimeMessage: MimeMessage) {
        runCatching { javaMailSender.send(mimeMessage) }
            .onSuccess {
                mailRepository.save(mailResult(true))
            }
            .onFailure {
                mailRepository.save(mailResult(false))
                log.error(it) { "MailServiceImpl.sendMail() :: FAILED" }
            }

    }
}

class Reserve(
    private val waitTime: Duration,
    private val decoratedMailClient: MailClient
) : MailClient {
    override fun sendMail(mimeMessage: MimeMessage) {

        CompletableFuture.runAsync(
            { decoratedMailClient.sendMail(mimeMessage) },
            CompletableFuture.delayedExecutor(
                waitTime.inWholeMilliseconds,
                TimeUnit.MILLISECONDS
            )
        )

    }
}