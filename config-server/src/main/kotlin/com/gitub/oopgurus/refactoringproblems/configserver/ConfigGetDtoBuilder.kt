package com.gitub.oopgurus.refactoringproblems.configserver

class ConfigGetDtoBuilder: ConfigVisitor {
    private var idGet: () -> Long = { throw IllegalStateException() }
    private var propertiesGet: () -> Map<String, String> = { throw IllegalStateException() }
    private var descriptionsGet: () -> List<String> = { throw IllegalStateException() }
    private var systemDto: SystemDto? = null
    private var personDtoList: MutableList<PersonDto> = mutableListOf()

    override fun id(id: Long) {
        idGet = { id }
    }

    override fun person(person: Person) {
        val builder = PersonDtoBuilderVisitor()
        person.accept(builder)
        personDtoList.add(builder.build())
    }

    override fun properties(properties: Map<String, String>) {
        propertiesGet = { properties }
    }

    override fun system(system: System) {
        val builder = SystemDtoBuilderVisitor()
        system.accept(builder)
        systemDto = builder.build()
    }

    override fun descriptions(descriptions: List<String>) {
        descriptionsGet = { descriptions }
    }

    fun build(): ConfigGetDto {
        return ConfigGetDto(
            id = idGet(),
            isValidSystem = systemDto != null,
            system = systemDto,
            persons = personDtoList,
            properties = propertiesGet(),
            descriptions = descriptionsGet(),
        )
    }
}