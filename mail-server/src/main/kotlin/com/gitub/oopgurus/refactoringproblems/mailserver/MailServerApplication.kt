package com.gitub.oopgurus.refactoringproblems.mailserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class MailServerApplication

fun main(args: Array<String>) {
	runApplication<MailServerApplication>(*args)
}
