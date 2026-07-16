package com.codeframe78.twentyfourseven.player.data

import org.junit.Assert.assertThrows
import org.junit.Test

class ContactReportResultParserTest {
    private val parser = ContactReportResultParser()

    @Test
    fun `known confirmation wins even when sidebar mentions security code`() {
        parser.requireConfirmed(
            """
                <html><body>
                  <aside><label>Security Code</label></aside>
                  <main>Thank you for contacting us. Your message has been sent.</main>
                </body></html>
            """.trimIndent(),
        )
    }

    @Test
    fun `returned contact form is a definite rejection`() {
        val error = assertThrows(ReportSubmissionRejectedException::class.java) {
            parser.requireConfirmed(
                """
                    <form>
                      <input name="sender_name">
                      <input name="sender_email">
                      <textarea name="message"></textarea>
                      <input name="random_num">
                    </form>
                """.trimIndent(),
            )
        }

        assert(error.message.orEmpty().contains("rejected"))
    }

    @Test
    fun `unrecognized page is indeterminate rather than a guessed failure`() {
        assertThrows(ReportConfirmationUnknownException::class.java) {
            parser.requireConfirmed("<aside>Security Code</aside><main>Contact Us</main>")
        }
    }
}
