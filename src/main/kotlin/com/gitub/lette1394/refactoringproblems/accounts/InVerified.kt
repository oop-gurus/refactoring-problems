package com.gitub.lette1394.refactoringproblems.accounts

class InVerified: AccountState {
  override fun verify(onVerify: () -> Any): AccountState {
    onVerify()
    return Verified()
  }

  override fun freeze(onFreeze: () -> Any): AccountState {
    throw RuntimeException("Account is inverified")
  }

  override fun close(onClose: () -> Any): AccountState {
    onClose()
    return Closed()
  }

  override fun withdraw(onWithdraw: () -> Any): AccountState {
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