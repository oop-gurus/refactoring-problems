package com.gitub.lette1394.refactoringproblems.accounts

class Closed: AccountState {
  override fun verify(onVerify: () -> Any): AccountState {
    return this
  }

  override fun freeze(onFreeze: () -> Any): AccountState {
    return this
  }

  override fun close(onClose: () -> Any): AccountState {
    return this
  }

  override fun withdraw(onWithdraw: () -> Any): AccountState {
    return this
  }

  override fun deposit(onDeposit: () -> Any): AccountState {
    return this
  }

  override fun snapshot(): AccountSnapshot {
    return AccountSnapshot("closed")
  }
}