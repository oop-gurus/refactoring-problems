package com.gitub.lette1394.refactoringproblems.accounts

class Verified: AccountState {
  override fun verify(onVerify: () -> Any) {
    onVerify()
  }

  override fun freeze(onFreeze: () -> Any) {
    onFreeze()
  }

  override fun close(onClose: () -> Any) {
    onClose()
  }

  override fun withdraw(onWithdraw: () -> Any) {
    onWithdraw()
  }

  override fun deposit(onDeposit: () -> Any) {
    onDeposit()
  }
}