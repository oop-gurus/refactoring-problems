package com.gitub.lette1394.refactoringproblems.accounts

interface AccountState {
  fun verify(onVerify: () -> Any): AccountState;
  fun freeze(onFreeze: () -> Any): AccountState;
  fun close(onClose: () -> Any): AccountState;
  fun withdraw(onWithdraw: () -> Any): AccountState;
  fun deposit(onDeposit: () -> Any): AccountState;
  fun snapshot(): AccountSnapshot;
}