package com.codeframe78.twentyfourseven.player.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CurrentTrackArtworkParserTest {
    private val parser = CurrentTrackArtworkParser()

    @Test
    fun `uses verified five hundred pixel cover path for an explicit asin`() {
        val response = JSONObject().put("ASIN", "B00Q5M2SYS").toString()

        assertEquals(
            "https://streamingsoundtracks.com/images/cover/500/B00Q5M2SYS.jpg",
            parser.parse(response, "https://streamingsoundtracks.com/"),
        )
    }

    @Test
    fun `upgrades a station cover link using its explicit album identifier`() {
        val response = JSONObject()
            .put("CoverLink", "https://adagio.fm/images/cover/040/B012345678.jpg")
            .toString()

        assertEquals(
            "https://adagio.fm/images/cover/500/B012345678.jpg",
            parser.parse(response, "https://adagio.fm/"),
        )
    }

    @Test
    fun `rejects artwork outside the selected station domain`() {
        val response = JSONObject()
            .put("CoverLink", "https://example.com/images/cover/040/B012345678.jpg")
            .toString()

        assertNull(parser.parse(response, "https://death.fm/"))
    }
}
