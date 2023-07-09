package com.gitub.lette1394.refactoringproblems.accounts

class Closed: AccountState {
  override fun verify(onVerify: () -> Any): AccountState {
    onVerify()
    return this
  }

  override fun freeze(onFreeze: () -> Any): AccountState {
    log.info { "계좌가 이미 닫혀있습니다. 동결할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is closed")
  }

  override fun close(onClose: () -> Any): AccountState {
    onClose()
    return this
  }

  override fun withdraw(onWithdraw: () -> Any): AccountState {
    log.info { "계좌가 이미 닫혀있습니다. 출금할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is closed")
  }

  override fun deposit(onDeposit: () -> Any): AccountState {
    log.info { "계좌가 이미 닫혀있습니다. 입금할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is closed")
  }

  override fun snapshot(): AccountSnapshot {
    return AccountSnapshot("closed")
  }
}