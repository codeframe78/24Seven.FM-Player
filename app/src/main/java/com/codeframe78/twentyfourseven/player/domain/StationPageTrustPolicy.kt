package com.codeframe78.twentyfourseven.player.domain

import java.net.URI

object StationPageTrustPolicy {
    fun trustedEmailRecipient(station: Station?, page: StationPage): String? {
        if (station == null || !station.capabilities.supportsSecondaryContent) return null
        if (page !in station.secondaryPages || page.kind != StationPageKind.Contact) return null

        val pageUri = page.url.toUriOrNull() ?: return null
        if (!pageUri.scheme.equals("mailto", ignoreCase = true)) return null
        if (pageUri.rawSchemeSpecificPart != PLAYER_CONTACT_EMAIL) return null
        if (pageUri.rawQuery != null || pageUri.rawFragment != null) return null
        return PLAYER_CONTACT_EMAIL
    }

    fun trustedUrl(station: Station?, page: StationPage): String? {
        if (station == null || !station.capabilities.supportsSecondaryContent) return null
        if (page !in station.secondaryPages) return null
        if (page.kind == StationPageKind.Contact || page.kind == StationPageKind.Membership) return null

        val stationUri = station.websiteUrl.toUriOrNull() ?: return null
        val pageUri = page.url.toUriOrNull() ?: return null
        if (!stationUri.scheme.equals("https", ignoreCase = true)) return null
        if (!pageUri.scheme.equals("https", ignoreCase = true)) return null
        if (pageUri.rawUserInfo != null || pageUri.rawFragment != null) return null
        if (pageUri.port != -1 && pageUri.port != 443) return null
        val stationHost = canonicalHost(stationUri.host) ?: return null
        val pageHost = canonicalHost(pageUri.host) ?: return null
        if (pageHost != stationHost) return null

        val allowedPath = when (page.kind) {
            StationPageKind.Website -> pageUri.path == "/" && pageUri.rawQuery == null
            else -> pageUri.path == "/modules.php" && !pageUri.rawQuery.isNullOrBlank()
        }
        return page.url.takeIf { allowedPath }
    }

    private fun String.toUriOrNull(): URI? = runCatching { URI(this) }.getOrNull()

    private fun canonicalHost(host: String?): String? = host
        ?.lowercase()
        ?.removePrefix("www.")
}
