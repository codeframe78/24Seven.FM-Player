package com.codeframe78.twentyfourseven.player.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class AuthLoginResultParserTest {
    private val parser = AuthLoginResultParser()

    @Test
    fun `welcome identity and same-origin logout identify signed-in account`() {
        assertEquals(
            "Listener",
            parser.parseSignedInDisplayName(signedInPage(), "https://streamingsoundtracks.com/", "Listener"),
        )
    }

    @Test
    fun `remaining password field rejects response`() {
        assertThrows(IOException::class.java) {
            parser.parseSignedInDisplayName(
                signedInPage(extra = "<input name=user_password type=password>"),
                "https://streamingsoundtracks.com/",
                "Listener",
            )
        }
    }

    @Test
    fun `cross-origin logout is rejected`() {
        assertThrows(IOException::class.java) {
            parser.parseSignedInDisplayName(
                signedInPage(logout = "https://example.com/modules.php?name=Your_Account&op=logout"),
                "https://streamingsoundtracks.com/",
                "Listener",
            )
        }
    }

    @Test
    fun `unrelated account-derived links are ignored`() {
        assertEquals(
            "Listener",
            parser.parseSignedInDisplayName(
                signedInPage(extra = "<a href='/unrelated?account_derived=sensitive'>Other</a>"),
                "https://streamingsoundtracks.com/",
                "Listener",
            ),
        )
    }

    private fun signedInPage(
        logout: String = "/modules.php?name=Your_Account&op=logout",
        extra: String = "",
    ) = """
        <html><body>
          <p>Welcome, Listener.</p>
          <a href="$logout">Logout</a>
          $extra
        </body></html>
    """.trimIndent()
}
