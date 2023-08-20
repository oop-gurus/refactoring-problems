package com.gitub.oopgurus.refactoringproblems.mailserver

class ToAddressSupplierFactory(
    private val mailSpamService: MailSpamService,
    private val toAddress: String,
) {
    fun create(): () -> String {
        mailSpamService.needBlockByDomainName(toAddress).let {
            if (it) {
                throw RuntimeException("도메인 차단")
            }
        }
        mailSpamService.needBlockByRecentSuccess(toAddress).let {
            if (it) {
                throw RuntimeException("최근 메일 발송 실패로 인한 차단")
            }
        }
        Regex(".+@.*\\..+").matches(toAddress).let {
            if (it.not()) {
                throw RuntimeException("이메일 형식 오류")
            }
        }

        return { toAddress }
    }
}