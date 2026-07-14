package com.codeframe78.twentyfourseven.player.data

import org.jsoup.Jsoup
import java.io.IOException
import java.net.URI

internal data class LoginChallenge(
    val actionUrl: String,
    val imageUrl: String,
    val challengeToken: String,
)

internal class AuthLoginPageParser {
    fun parse(html: String, origin: String): LoginChallenge {
        val originUri = URI(origin)
        require(originUri.scheme == "https" && originUri.path == "/")
        val document = Jsoup.parse(html, origin)
        val form = document.select("form").firstOrNull { candidate ->
            candidate.selectFirst("input[name=username]") != null &&
                candidate.selectFirst("input[name=user_password]") != null &&
                candidate.selectFirst("input[name=gfx_check]") != null
        } ?: throw IOException("Login form was not found")

        val operation = form.selectFirst("input[name=op]")?.attr("value")
        if (operation != "login") throw IOException("Login operation was not recognized")

        val token = form.selectFirst("input[name=random_num]")?.attr("value").orEmpty()
        if (!token.matches(SIX_DIGIT_CHALLENGE)) throw IOException("Login challenge was not recognized")

        val action = sameOriginHttpsUrl(form.absUrl("action"), originUri, "login action")
        val imageElement = form.selectFirst("img[alt=Security Code]")
            ?: throw IOException("Security code image was not found")
        val image = sameOriginHttpsUrl(imageElement.absUrl("src"), originUri, "security code image")
        return LoginChallenge(action, image, token)
    }

    private fun sameOriginHttpsUrl(value: String, origin: URI, label: String): String {
        val uri = runCatching { URI(value) }.getOrNull()
            ?: throw IOException("Invalid $label")
        if (
            uri.scheme != "https" ||
            !uri.host.equals(origin.host, ignoreCase = true) ||
            uri.port != origin.port
        ) {
            throw IOException("Untrusted $label")
        }
        return uri.toASCIIString()
    }

    private companion object {
        val SIX_DIGIT_CHALLENGE = Regex("^[0-9]{6}$")
    }
}
