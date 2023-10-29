package com.gitub.oopgurus.refactoringproblems.configserver

class Config(
    private val id: Long,
    private val properties: Properties,
    private val persons: List<Person>,
    private val system: System?,
): 정보를_가진놈 {
    override fun 너한테_내정보_허락해줄께_정보_넘겨줄테니_너가_값_채워넣어(도둑: 정보를_훔쳐갈놈) {
        도둑.원하는_정보만_채우기(this)
    }

}