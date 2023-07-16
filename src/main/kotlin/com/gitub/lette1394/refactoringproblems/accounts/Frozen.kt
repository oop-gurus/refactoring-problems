package com.gitub.lette1394.refactoringproblems.accounts

class Frozen : AccountState {
  override fun verify(onVerify: () -> Any) {
    onVerify()
  }

  override fun freeze(onFreeze: () -> Any) {
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