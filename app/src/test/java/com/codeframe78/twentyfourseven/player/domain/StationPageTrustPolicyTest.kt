package com.codeframe78.twentyfourseven.player.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StationPageTrustPolicyTest {
    private val page = StationPage(
        kind = StationPageKind.Forums,
        title = "Forums",
        description = "Community discussions",
        url = "https://streamingsoundtracks.com/modules.php?name=Forums",
    )
    private val station = Station(
        id = StationId("sst"),
        name = "StreamingSoundtracks.com",
        shortName = "SST",
        description = "Scores",
        websiteUrl = "https://www.streamingsoundtracks.com/",
        capabilities = StationCapabilities(supportsSecondaryContent = true),
        secondaryPages = listOf(page),
    )

    @Test
    fun `allows an exact catalog page on the station https origin`() {
        assertEquals(page.url, StationPageTrustPolicy.trustedUrl(station, page))
    }

    @Test
    fun `rejects a page that is not in the selected station catalog`() {
        val unlisted = page.copy(url = "https://streamingsoundtracks.com/modules.php?name=Private_Messages")

        assertNull(StationPageTrustPolicy.trustedUrl(station, unlisted))
    }

    @Test
    fun `rejects cross origin cleartext credentials fragments and nonstandard ports`() {
        listOf(
            "https://example.com/modules.php?name=Forums",
            "http://streamingsoundtracks.com/modules.php?name=Forums",
            "https://listener@streamingsoundtracks.com/modules.php?name=Forums",
            "https://streamingsoundtracks.com:8443/modules.php?name=Forums",
            "https://streamingsoundtracks.com/modules.php?name=Forums#private",
        ).forEach { unsafeUrl ->
            val unsafe = page.copy(url = unsafeUrl)
            val unsafeStation = station.copy(secondaryPages = listOf(unsafe))

            assertNull(unsafeUrl, StationPageTrustPolicy.trustedUrl(unsafeStation, unsafe))
        }
    }

    @Test
    fun `rejects content when the station capability is disabled`() {
        assertNull(
            StationPageTrustPolicy.trustedUrl(
                station.copy(capabilities = StationCapabilities()),
                page,
            ),
        )
    }

    @Test
    fun `website kind only allows the exact origin root`() {
        val website = StationPage(
            StationPageKind.Website,
            "Station website",
            "News",
            "https://streamingsoundtracks.com/",
        )
        val websiteStation = station.copy(secondaryPages = listOf(website))

        assertEquals(website.url, StationPageTrustPolicy.trustedUrl(websiteStation, website))
        val unsafe = website.copy(url = "https://streamingsoundtracks.com/news")
        assertNull(
            StationPageTrustPolicy.trustedUrl(
                websiteStation.copy(secondaryPages = listOf(unsafe)),
                unsafe,
            ),
        )
    }

    @Test
    fun `contact kind allows only the fixed monitored email recipient`() {
        val contact = StationPage(
            StationPageKind.Contact,
            "Contact Us",
            "Email the Player contact",
            "mailto:$PLAYER_CONTACT_EMAIL",
        )
        val contactStation = station.copy(secondaryPages = listOf(contact))

        assertEquals(
            PLAYER_CONTACT_EMAIL,
            StationPageTrustPolicy.trustedEmailRecipient(contactStation, contact),
        )
        assertNull(StationPageTrustPolicy.trustedUrl(contactStation, contact))

        listOf(
            "mailto:other@example.com",
            "mailto:$PLAYER_CONTACT_EMAIL?subject=Injected",
            "https://streamingsoundtracks.com/modules.php?name=Contact_Us",
        ).forEach { unsafeUrl ->
            val unsafe = contact.copy(url = unsafeUrl)
            assertNull(
                unsafeUrl,
                StationPageTrustPolicy.trustedEmailRecipient(
                    contactStation.copy(secondaryPages = listOf(unsafe)),
                    unsafe,
                ),
            )
        }
    }

    @Test
    fun `membership transaction route remains blocked even if catalogued`() {
        val membership = StationPage(
            StationPageKind.Membership,
            "VIP membership",
            "Station membership information",
            "https://streamingsoundtracks.com/modules.php?name=VIP_Subscribe",
        )
        val membershipStation = station.copy(secondaryPages = listOf(membership))

        assertNull(StationPageTrustPolicy.trustedUrl(membershipStation, membership))
        assertNull(StationPageTrustPolicy.trustedEmailRecipient(membershipStation, membership))
    }
}
