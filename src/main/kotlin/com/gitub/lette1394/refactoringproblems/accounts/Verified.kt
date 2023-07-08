package com.gitub.lette1394.refactoringproblems.accounts

class Verified: AccountState {
  override fun verify(onVerify: () -> Any): AccountState {
    onVerify()
    return this
  }

  override fun freeze(onFreeze: () -> Any): AccountState {
    onFreeze()
    return Frozen()
  }

  override fun close(onClose: () -> Any): AccountState {
    onClose()
    return Closed()
  }

  override fun withdraw(onWithdraw: () -> Any): AccountState {
    onWithdraw()
    return this
  }

  override fun deposit(onDeposit: () -> Any): AccountState {
    onDeposit()
    return this
  }

  override fun snapshot(): AccountSnapshot {
    return AccountSnapshot("verified")
  }
}