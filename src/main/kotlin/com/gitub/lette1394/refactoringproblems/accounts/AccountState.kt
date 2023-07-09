package com.gitub.lette1394.refactoringproblems.accounts

import mu.KLogger
import mu.KotlinLogging

interface AccountState {
  val log: KLogger
    get() = KotlinLogging.logger {}

  fun verify(onVerify: () -> Any)
  fun freeze(onFreeze: () -> Any)
  fun close(onClose: () -> Any)
  fun withdraw(onWithdraw: () -> Any)
  fun deposit(onDeposit: () -> Any)
}