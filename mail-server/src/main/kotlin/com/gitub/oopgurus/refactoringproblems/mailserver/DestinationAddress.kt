package com.gitub.oopgurus.refactoringproblems.mailserver

class DestinationAddress(value: String): Address(value) {
    init {
        if ("naver.com" in value
            || "gmail.com" in value) {
            throw RuntimeException("도메인 차단")
        }
    }
}
