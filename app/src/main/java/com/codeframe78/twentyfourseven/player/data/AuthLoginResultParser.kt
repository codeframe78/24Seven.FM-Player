package com.codeframe78.twentyfourseven.player.data

import org.jsoup.Jsoup
import java.io.IOException
import java.net.URI

internal class AuthLoginResultParser {
    fun parseSignedInDisplayName(html: String, origin: String, username: String): String {
        val originUri = URI(origin)
        require(originUri.scheme == "https" && originUri.path == "/")
        val document = Jsoup.parse(html, origin)
        if (document.selectFirst("input[name=user_password]") != null) {
            throw IOException("Station still shows the login form")
        }
        val hasTrustedLogout = document.select("a[href]").any { link ->
            val uri = runCatching { URI(link.absUrl("href")) }.getOrNull() ?: return@any false
            uri.scheme == "https" &&
                uri.host.equals(originUri.host, ignoreCase = true) &&
                uri.port == originUri.port &&
                uri.path == "/modules.php" &&
                queryValue(uri.rawQuery, "name") == "Your_Account" &&
                queryValue(uri.rawQuery, "op") == "logout"
        }
        val hasWelcome = document.text().contains("Welcome, $username", ignoreCase = true)
        if (!hasTrustedLogout || !hasWelcome) throw IOException("Signed-in account was not recognized")
        return username
    }

    private fun queryValue(query: String?, name: String): String? = query
        ?.split('&')
        ?.mapNotNull { part -> part.split('=', limit = 2).takeIf { it.size == 2 } }
        ?.firstOrNull { it[0] == name }
        ?.get(1)
}
