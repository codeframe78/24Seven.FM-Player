package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.FavoriteTrack
import com.codeframe78.twentyfourseven.player.domain.StationId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets

internal interface FavoriteTracksRemoteDataSource {
    suspend fun load(stationId: StationId): List<FavoriteTrack>
}

internal class StationFavoriteTracksRemoteDataSource(
    private val sessionStore: AuthSessionStore = InMemoryAuthSessionStore(),
    private val parser: FavoriteTracksPageParser = FavoriteTracksPageParser(),
    private val connectionFactory: (URI) -> HttpURLConnection = {
        it.toURL().openConnection() as HttpURLConnection
    },
) : FavoriteTracksRemoteDataSource {
    override suspend fun load(stationId: StationId): List<FavoriteTrack> = withContext(Dispatchers.IO) {
        val origin = origin(stationId)
        val manager = authenticatedCookieManager(stationId, origin)
        val discovery = request(URI(origin).resolve(FAVORITES_PATH), origin, manager, DISCOVERY_LIMIT)
        val listUrl = parser.parseListUrl(discovery, origin)
        parser.parseTracks(request(URI(listUrl), origin, manager, LIST_LIMIT), origin)
    }

    private fun authenticatedCookieManager(stationId: StationId, origin: String): CookieManager {
        val uri = URI(origin)
        val cookies = sessionStore.load(stationId, uri.host)
        if (cookies.isEmpty()) throw FavoritesAuthenticationRequiredException()
        return CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER).also { manager ->
            cookies.forEach { manager.cookieStore.add(uri, it) }
        }
    }

    private fun request(
        initialUri: URI,
        origin: String,
        manager: CookieManager,
        limit: Int,
    ): String {
        val expected = URI(origin)
        var uri = initialUri
        repeat(MAX_REDIRECTS + 1) { redirectCount ->
            requireSameOrigin(uri, expected)
            val connection = connectionFactory(uri)
            try {
                connection.connectTimeout = CONNECT_TIMEOUT_MILLIS
                connection.readTimeout = READ_TIMEOUT_MILLIS
                connection.instanceFollowRedirects = false
                connection.setRequestProperty("Accept", "text/html")
                connection.setRequestProperty("User-Agent", USER_AGENT)
                manager.get(uri, emptyMap()).forEach { (name, values) ->
                    connection.setRequestProperty(name, values.joinToString("; "))
                }
                val status = connection.responseCode
                manager.put(uri, connection.headerFields.filterKeys { it != null })
                if (status in REDIRECT_STATUSES) {
                    if (redirectCount == MAX_REDIRECTS) throw IOException("Too many favorites redirects")
                    val location = connection.getHeaderField("Location")
                        ?: throw IOException("Favorites redirect was invalid")
                    uri = uri.resolve(location)
                    return@repeat
                }
                if (status !in 200..299) throw IOException("Station returned HTTP $status")
                return connection.inputStream.bufferedReader(StandardCharsets.ISO_8859_1).use {
                    it.readBounded(limit)
                }
            } finally {
                connection.disconnect()
            }
        }
        throw IOException("Favorites request did not complete")
    }

    private fun requireSameOrigin(uri: URI, origin: URI) {
        if (uri.scheme != "https" || !uri.host.equals(origin.host, true) || uri.port != origin.port) {
            throw IOException("Untrusted favorites destination")
        }
    }

    private fun origin(stationId: StationId): String = ORIGINS[stationId]
        ?: throw IOException("Unsupported station")

    private companion object {
        const val FAVORITES_PATH = "/modules.php?name=Favorites"
        const val USER_AGENT = "24Seven.FM-Player/0.1 (Android; unofficial non-commercial client)"
        const val CONNECT_TIMEOUT_MILLIS = 15_000
        const val READ_TIMEOUT_MILLIS = 30_000
        const val DISCOVERY_LIMIT = 512_000
        const val LIST_LIMIT = 5_000_000
        const val MAX_REDIRECTS = 5
        val REDIRECT_STATUSES = setOf(301, 302, 303, 307, 308)
        val ORIGINS = mapOf(
            StationId("sst") to "https://streamingsoundtracks.com/",
            StationId("1980s") to "https://1980s.fm/",
            StationId("adagio") to "https://adagio.fm/",
            StationId("death") to "https://death.fm/",
            StationId("entranced") to "https://entranced.fm/",
        )
    }
}
