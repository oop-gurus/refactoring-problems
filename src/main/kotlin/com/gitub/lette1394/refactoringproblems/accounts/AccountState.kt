package com.gitub.lette1394.refactoringproblems.accounts

import mu.KLogger
import mu.KotlinLogging

interface AccountState {
  val log: KLogger
    get() = KotlinLogging.logger {}

  fun verify(onVerify: () -> Any): AccountState;
  fun freeze(onFreeze: () -> Any): AccountState;
  fun close(onClose: () -> Any): AccountState;
  fun withdraw(onWithdraw: () -> Any): AccountState;
  fun deposit(onDeposit: () -> Any): AccountState;
  fun snapshot(): AccountSnapshot;
}