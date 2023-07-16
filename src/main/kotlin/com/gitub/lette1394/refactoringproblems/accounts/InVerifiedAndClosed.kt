package com.gitub.lette1394.refactoringproblems.accounts

class InVerifiedAndClosed: AccountState {
  override fun verify(onVerify: () -> Unit) {
    onVerify()
  }

  override fun freeze(onFreeze: () -> Unit) {
    log.info { "확인되지 않은 계좌는 동결할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is inverified")
  }

  override fun close(onClose: () -> Unit) {
  }

  override fun withdraw(onWithdraw: () -> Unit) {
    log.info { "확인되지 않은 계좌는 출금할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is inverified")
  }

  override fun deposit(onDeposit: () -> Unit) {
    log.info { "계좌가 이미 닫혀있습니다. 입금할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is closed")
  }
}