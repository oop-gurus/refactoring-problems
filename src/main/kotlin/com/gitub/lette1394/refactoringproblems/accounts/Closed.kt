package com.gitub.lette1394.refactoringproblems.accounts

class Closed: AccountState {
  override fun verify(onVerify: () -> Any): AccountState {
    onVerify()
    return this
  }

  override fun freeze(onFreeze: () -> Any): AccountState {
    throw RuntimeException("Account is closed")
  }

  override fun close(onClose: () -> Any): AccountState {
    onClose()
    return this
  }

  override fun withdraw(onWithdraw: () -> Any): AccountState {
    throw RuntimeException("Account is closed")
  }

  override fun deposit(onDeposit: () -> Any): AccountState {
    throw RuntimeException("Account is closed")
  }

  override fun snapshot(): AccountSnapshot {
    return AccountSnapshot("closed")
  }
}