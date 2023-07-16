package com.gitub.lette1394.refactoringproblems.accounts

class VerifiedAndFrozenAndClosed: AccountState {
  override fun verify(onVerify: () -> Unit) {
  }

  override fun freeze(onFreeze: () -> Unit) {
  }

  override fun close(onClose: () -> Unit) {
  }

  override fun withdraw(onWithdraw: () -> Unit) {
    log.info { "계좌가 이미 닫혀있습니다. 출금할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is closed")
  }

  override fun deposit(onDeposit: () -> Unit) {
    log.info { "계좌가 이미 닫혀있습니다. 입금할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is closed")
  }
}