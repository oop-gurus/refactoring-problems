package com.gitub.lette1394.refactoringproblems.accounts

class InVerified: AccountState {
  override fun verify(onVerify: () -> Any): AccountState {
    onVerify()
    return Verified()
  }

  override fun freeze(onFreeze: () -> Any): AccountState {
    log.info { "확인되지 않은 계좌는 동결할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is inverified")
  }

  override fun close(onClose: () -> Any): AccountState {
    onClose()
    return Closed()
  }

  override fun withdraw(onWithdraw: () -> Any): AccountState {
    log.info { "확인되지 않은 계좌는 출금할 수 없습니다. 요청을 무시합니다." }
    throw RuntimeException("Account is inverified")
  }

  override fun deposit(onDeposit: () -> Any): AccountState {
    onDeposit()
    return this
  }

  override fun snapshot(): AccountSnapshot {
    return AccountSnapshot("created");
  }
}