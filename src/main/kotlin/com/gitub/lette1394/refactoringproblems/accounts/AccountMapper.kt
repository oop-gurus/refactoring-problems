package com.gitub.lette1394.refactoringproblems.accounts

object AccountMapper {

    fun toAccount(entity: AccountEntity): Account {
        return Account(
                accountId = entity.id,
                isVerified = entity.isVerified,
                isClosed = entity.isClosed,
                isFrozen = entity.isFrozen,
                balance = entity.balance,
                state = getAccountState(entity)
        )
    }

    private fun getAccountState(entity: AccountEntity) =
            if (!entity.isVerified) {
                if (entity.isClosed) {
                    InVerifiedAndClosed()
                }
                InVerified()
            } else {
                if (entity.isClosed) {
                    if(entity.isFrozen) {
                        VerifiedAndFrozenAndClosed()
                    }
                    Closed()
                }
                if(entity.isFrozen) {
                    Frozen()
                }
                Verified()
            }
}