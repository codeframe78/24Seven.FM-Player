package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.NowPlayingArtworkRepository
import com.codeframe78.twentyfourseven.player.domain.StationId
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class StationNowPlayingArtworkRepository internal constructor(
    private val parser: CurrentTrackArtworkParser = CurrentTrackArtworkParser(),
) : NowPlayingArtworkRepository {
    override suspend fun fetchArtwork(stationId: StationId): String? = withContext(Dispatchers.IO) {
        val domain = domains[stationId] ?: throw IOException("Unsupported station")
        val origin = "https://$domain/"
        val connection = URI("${origin}soap/FM24sevenJSON.php?action=GetCurrentlyPlaying")
            .toURL()
            .openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = REQUEST_TIMEOUT_MILLIS
            connection.readTimeout = REQUEST_TIMEOUT_MILLIS
            connection.instanceFollowRedirects = true
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Referer", "${origin}player.php")
            connection.setRequestProperty("User-Agent", USER_AGENT)
            val status = connection.responseCode
            if (status !in 200..299) throw IOException("Station returned HTTP $status")
            val response = connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                reader.readBounded(MAX_RESPONSE_CHARACTERS)
            }
            parser.parse(response, origin)
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val USER_AGENT = "24Seven.FM-Player/0.1 (Android; unofficial non-commercial client)"
        const val REQUEST_TIMEOUT_MILLIS = 10_000
        const val MAX_RESPONSE_CHARACTERS = 64_000
        val domains = mapOf(
            StationId("sst") to "streamingsoundtracks.com",
            StationId("1980s") to "1980s.fm",
            StationId("adagio") to "adagio.fm",
            StationId("death") to "death.fm",
            StationId("entranced") to "entranced.fm",
        )
    }
}

internal class CurrentTrackArtworkParser {
    fun parse(json: String, baseUrl: String): String? {
        val response = JSONObject(json)
        val origin = URI(baseUrl)
        val explicitAsin = response.optString("ASIN", "")
            .trim()
            .takeIf(ASIN::matches)
        val coverUri = response.optString("CoverLink", "")
            .trim()
            .takeIf(String::isNotEmpty)
            ?.let { runCatching { origin.resolve(it) }.getOrNull() }
            ?.takeIf { isSafeStationUrl(it, origin) }
        val coverAsin = coverUri?.path
            ?.substringAfterLast('/')
            ?.substringBeforeLast('.')
            ?.takeIf(ASIN::matches)
        val asin = explicitAsin ?: coverAsin
        return if (asin != null) {
            URI(origin.scheme, null, origin.host, origin.port, "/images/cover/500/$asin.jpg", null, null).toString()
        } else {
            coverUri?.toString()
        }
    }

    private fun isSafeStationUrl(candidate: URI, origin: URI): Boolean =
        candidate.scheme.equals("https", ignoreCase = true) &&
            candidate.host.equals(origin.host, ignoreCase = true) &&
            candidate.userInfo == null &&
            candidate.port in setOf(-1, origin.port)

    private companion object {
        val ASIN = Regex("[A-Za-z0-9]{10}")
    }
}
