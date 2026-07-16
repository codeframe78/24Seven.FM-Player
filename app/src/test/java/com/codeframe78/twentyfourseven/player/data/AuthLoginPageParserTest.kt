package com.codeframe78.twentyfourseven.player.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class AuthLoginPageParserTest {
    private val parser = AuthLoginPageParser()

    @Test
    fun `verified form produces same-origin challenge`() {
        val challenge = parser.parse(loginPage(), "https://adagio.fm/")

        assertEquals("https://adagio.fm/modules.php?name=Your_Account", challenge.actionUrl)
        assertEquals(
            "https://adagio.fm/modules.php?name=Your_Account&gfx=gfx&random_num=123456",
            challenge.imageUrl,
        )
        assertEquals("123456", challenge.challengeToken)
    }

    @Test
    fun `cross-origin security image is rejected`() {
        assertThrows(IOException::class.java) {
            parser.parse(loginPage(imageUrl = "https://example.com/code.png"), "https://adagio.fm/")
        }
    }

    @Test
    fun `unexpected challenge format is rejected`() {
        assertThrows(IOException::class.java) {
            parser.parse(loginPage(token = "not-a-code"), "https://adagio.fm/")
        }
    }

    @Test
    fun `unexpected operation is rejected`() {
        assertThrows(IOException::class.java) {
            parser.parse(loginPage(operation = "register"), "https://adagio.fm/")
        }
    }

    private fun loginPage(
        token: String = "123456",
        operation: String = "login",
        imageUrl: String = "/modules.php?name=Your_Account&gfx=gfx&random_num=$token",
    ) = """
        <form method="post" action="/modules.php?name=Your_Account">
          <input name="username" type="text">
          <input name="user_password" type="password">
          <input name="gfx_check" type="text">
          <input name="random_num" type="hidden" value="$token">
          <input name="op" type="hidden" value="$operation">
          <img src="$imageUrl" alt="Security Code">
        </form>
    """.trimIndent()
}
