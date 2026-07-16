package com.codeframe78.twentyfourseven.player.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.codeframe78.twentyfourseven.player.domain.AbuseReportCategory
import com.codeframe78.twentyfourseven.player.domain.AbuseReportKind
import com.codeframe78.twentyfourseven.player.domain.AbuseReportSource
import com.codeframe78.twentyfourseven.player.domain.AbuseReportStatus
import com.codeframe78.twentyfourseven.player.domain.AbuseReportSubmission
import com.codeframe78.twentyfourseven.player.domain.AbuseReportTarget
import com.codeframe78.twentyfourseven.player.domain.CommunitySafetyRepository
import com.codeframe78.twentyfourseven.player.domain.StationId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.net.CookieManager
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class SharedPreferencesCommunitySafetyRepositoryTest {
    @Test
    fun adultTermsVisibilityAndStationBlocksSurviveRepositoryRecreation() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "m23-community-safety-test"
        val stored = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        stored.edit().clear().commit()
        try {
            repository(context, name).apply {
                submitAgeScreen(1990, 4, 3)
                acceptTerms()
                setCommunityContentVisible(true)
                blockUser(StationId("sst"), " MorG Hubby ")
            }

            val restored = repository(context, name).observeSafety().first()
            assertTrue(restored.canViewCommunityContent)
            assertTrue(restored.isBlocked(StationId("sst"), "morg hubby"))
            assertFalse(restored.isBlocked(StationId("adagio"), "morg hubby"))
        } finally {
            stored.edit().clear().commit()
        }
    }

    @Test
    fun reportStateUsesBoundedTransientTargetAndDoesNotPersistSubmission() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "m23-community-report-test"
        val stored = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        stored.edit().clear().commit()
        val remote = FakeAbuseReportRemoteDataSource()
        try {
            val repository = SharedPreferencesCommunitySafetyRepository(
                context = context,
                remote = remote,
                today = { LocalDate.of(2026, 7, 15) },
                preferencesName = name,
            )
            repository.beginReport(
                StationId("sst"),
                AbuseReportTarget(
                    AbuseReportKind.Content,
                    AbuseReportSource.Chat,
                    reportedUser = "Reported Listener",
                    displayedTimestamp = "12:34",
                    contentSnapshot = "x".repeat(800),
                ),
            )
            assertEquals(AbuseReportStatus.Ready, repository.observeReport().first().status)

            repository.submitReport(
                AbuseReportSubmission(
                    reporterName = "Reporter",
                    reporterEmail = "reporter@example.com",
                    category = AbuseReportCategory.Harassment,
                    optionalDetails = "Harmless automated test",
                    securityCode = "123",
                ),
            )

            assertEquals(AbuseReportStatus.Submitted, repository.observeReport().first().status)
            assertEquals(500, remote.target?.contentSnapshot?.length)
            assertEquals("Reporter", remote.submission?.reporterName)

            val recreated = repository(context, name)
            assertEquals(AbuseReportStatus.Idle, recreated.observeReport().first().status)
        } finally {
            stored.edit().clear().commit()
        }
    }

    @Test
    fun indeterminateReportConfirmationSuppressesRetryToPreventDuplicates() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "m23-community-report-indeterminate-test"
        val stored = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        stored.edit().clear().commit()
        try {
            val repository = SharedPreferencesCommunitySafetyRepository(
                context = context,
                remote = FakeAbuseReportRemoteDataSource(ReportConfirmationUnknownException()),
                today = { LocalDate.of(2026, 7, 15) },
                preferencesName = name,
            )
            repository.beginReport(
                StationId("sst"),
                AbuseReportTarget(
                    AbuseReportKind.User,
                    AbuseReportSource.Chat,
                    reportedUser = "Reported Listener",
                ),
            )
            repository.submitReport(
                AbuseReportSubmission(
                    reporterName = "Reporter",
                    reporterEmail = "reporter@example.com",
                    category = AbuseReportCategory.Other,
                    optionalDetails = "",
                    securityCode = "aB3",
                ),
            )

            val report = repository.observeReport().first()
            assertEquals(AbuseReportStatus.Error, report.status)
            assertFalse(report.retryAllowed)
            assertTrue(report.errorMessage.orEmpty().contains("Do not resend"))
        } finally {
            stored.edit().clear().commit()
        }
    }

    private fun repository(context: Context, name: String): CommunitySafetyRepository =
        SharedPreferencesCommunitySafetyRepository(
            context = context,
            remote = FakeAbuseReportRemoteDataSource(),
            today = { LocalDate.of(2026, 7, 15) },
            preferencesName = name,
        )

    private class FakeAbuseReportRemoteDataSource(
        private val submitFailure: IOException? = null,
    ) : AbuseReportRemoteDataSource {
        var target: AbuseReportTarget? = null
        var submission: AbuseReportSubmission? = null

        override suspend fun loadChallenge(stationId: StationId) = ContactReportChallenge(
            actionUrl = "https://streamingsoundtracks.com/modules.php?name=Contact_Us",
            captchaImageUrl = "https://streamingsoundtracks.com/?gfx=gfx_little&random_num=123",
            challengeToken = "123",
            cookies = CookieManager(),
        )

        override suspend fun submit(
            stationId: StationId,
            challenge: ContactReportChallenge,
            target: AbuseReportTarget,
            submission: AbuseReportSubmission,
        ) {
            submitFailure?.let { throw it }
            this.target = target
            this.submission = submission
        }
    }
}
