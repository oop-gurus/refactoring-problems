package com.gitub.oopgurus.refactoringproblems.accountsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class WebApiApplication

fun main(args: Array<String>) {
	runApplication<WebApiApplication>(*args)
}
