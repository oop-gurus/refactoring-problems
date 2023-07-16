package com.gitub.lette1394.refactoringproblems.accounts

class InVerified: AccountState {
  override fun verify(onVerify: () -> Unit) {
    onVerify()
  }

  override fun freeze(onFreeze: () -> Unit) {
    log.info { "확인되지 않은 계좌는 동결할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is inverified")
  }

  override fun melt(onMelt: () -> Unit) {
  }

  override fun close(onClose: () -> Unit) {
    onClose()
  }

  override fun withdraw(onWithdraw: () -> Unit) {
    log.info { "확인되지 않은 계좌는 출금할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is inverified")
  }

  override fun deposit(onDeposit: () -> Unit) {
    onDeposit()
  }
}