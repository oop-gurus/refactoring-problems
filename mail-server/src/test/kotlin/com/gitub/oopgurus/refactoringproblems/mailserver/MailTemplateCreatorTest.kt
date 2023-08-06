package com.gitub.oopgurus.refactoringproblems.mailserver

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class MailTemplateCreatorTest {
    private val mailTemplateRepository = mockk<MailTemplateRepository>()
    private val mailTemplateCreator = MailTemplateCreator(mailTemplateRepository)

    @Test
    fun create() {
        every {
            hint(MailTemplateEntity::class)
            mailTemplateRepository.save(any())
        } returns mockk()

        val createMailTemplateDtoList = listOf(
                CreateMailTemplateDto("HELLO_WORLD_01", HtmlBody("<html>\\n<h2>Hello 1<h2>\\n</html>")),
                CreateMailTemplateDto("HELLO_WORLD_02", HtmlBody("<html>\\n<h2>Hello 2<h2>\\n</html>")),
                CreateMailTemplateDto("HELLO_WORLD_03", HtmlBody("<html>\\n<h2>Hello 3<h2>\\n</html>")),
        )

        mailTemplateCreator.create(createMailTemplateDtoList)

        verify(exactly = 3) { mailTemplateRepository.save(any()) }
    }
}