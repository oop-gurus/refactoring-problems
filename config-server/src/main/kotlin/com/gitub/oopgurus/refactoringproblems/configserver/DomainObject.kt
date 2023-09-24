package com.gitub.oopgurus.refactoringproblems.configserver

interface DomainObject {
    fun okay_i_will_give_you_what_you_want(whatIWantToPerson: WhatIWantToPerson)
    fun okay_i_will_give_you_what_you_want(whatIWantToSystem: WhatIWantToSystem)
    fun okay_i_will_give_you_what_you_want(whatIWantToProperties: WhatIWantToProperties)
}