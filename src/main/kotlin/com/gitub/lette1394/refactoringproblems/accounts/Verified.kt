package com.gitub.lette1394.refactoringproblems.accounts

class Verified: AccountState {
  override fun verify(onVerify: () -> Unit) {
  }

  override fun freeze(onFreeze: () -> Unit) {
    onFreeze()
  }

  override fun close(onClose: () -> Unit) {
    onClose()
  }

  override fun withdraw(onWithdraw: () -> Unit) {
    onWithdraw()
  }

  override fun deposit(onDeposit: () -> Unit) {
    onDeposit()
  }
}