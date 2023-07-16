package com.gitub.lette1394.refactoringproblems.accounts

class Frozen : AccountState {
  override fun verify(onVerify: () -> Unit) {
    onVerify()
  }

  override fun freeze(onFreeze: () -> Unit) {
  }

  override fun melt(onMelt: () -> Unit) {
    onMelt()
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