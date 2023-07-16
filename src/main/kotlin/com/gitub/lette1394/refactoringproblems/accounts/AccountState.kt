package com.gitub.lette1394.refactoringproblems.accounts

import mu.KLogger
import mu.KotlinLogging

interface AccountState {
  val log: KLogger
    get() = KotlinLogging.logger {}

  fun verify(onVerify: () -> Unit)
  fun freeze(onFreeze: () -> Unit)
  fun close(onClose: () -> Unit)
  fun withdraw(onWithdraw: () -> Unit)
  fun deposit(onDeposit: () -> Unit)
}