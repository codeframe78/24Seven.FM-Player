package com.codeframe78.twentyfourseven.player.data

import android.content.Context
import com.codeframe78.twentyfourseven.player.domain.AbuseReportCategory
import com.codeframe78.twentyfourseven.player.domain.AbuseReportKind
import com.codeframe78.twentyfourseven.player.domain.AbuseReportSource
import com.codeframe78.twentyfourseven.player.domain.AbuseReportState
import com.codeframe78.twentyfourseven.player.domain.AbuseReportStatus
import com.codeframe78.twentyfourseven.player.domain.AbuseReportSubmission
import com.codeframe78.twentyfourseven.player.domain.AbuseReportTarget
import com.codeframe78.twentyfourseven.player.domain.AgeGateStatus
import com.codeframe78.twentyfourseven.player.domain.BlockedCommunityUser
import com.codeframe78.twentyfourseven.player.domain.CURRENT_COMMUNITY_TERMS_VERSION
import com.codeframe78.twentyfourseven.player.domain.CommunitySafetyRepository
import com.codeframe78.twentyfourseven.player.domain.CommunitySafetyState
import com.codeframe78.twentyfourseven.player.domain.StationId
import com.codeframe78.twentyfourseven.player.domain.isAdultOnDate
import com.codeframe78.twentyfourseven.player.domain.normalizedCommunityIdentity
import com.codeframe78.twentyfourseven.player.domain.validatedBirthDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

class SharedPreferencesCommunitySafetyRepository internal constructor(
    context: Context,
    private val remote: AbuseReportRemoteDataSource = StationContactAbuseReportRemoteDataSource(),
    private val today: () -> LocalDate = LocalDate::now,
    preferencesName: String = PREFERENCES_NAME,
) : CommunitySafetyRepository {
    private val preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    private val safety = MutableStateFlow(readSafety())
    private val report = MutableStateFlow(AbuseReportState())
    private var challenge: ContactReportChallenge? = null

    override fun observeSafety(): Flow<CommunitySafetyState> = safety.asStateFlow()

    override fun observeReport(): Flow<AbuseReportState> = report.asStateFlow()

    override suspend fun submitAgeScreen(year: Int, month: Int, day: Int) {
        val currentDate = today()
        val birthDate = validatedBirthDate(year, month, day, currentDate)
        if (birthDate == null) {
            safety.value = safety.value.copy(ageGateErrorMessage = "Enter a valid date of birth.")
            return
        }
        val result = if (isAdultOnDate(birthDate, currentDate)) AgeGateStatus.Adult else AgeGateStatus.Underage
        updateSafety(
            safety.value.copy(
                ageGateStatus = result,
                acceptedTermsVersion = safety.value.acceptedTermsVersion.takeIf { result == AgeGateStatus.Adult },
                communityContentVisible = safety.value.communityContentVisible && result == AgeGateStatus.Adult,
                ageGateErrorMessage = null,
            ),
        )
    }

    override suspend fun acceptTerms() {
        if (safety.value.ageGateStatus != AgeGateStatus.Adult) return
        updateSafety(safety.value.copy(acceptedTermsVersion = CURRENT_COMMUNITY_TERMS_VERSION))
    }

    override suspend fun setCommunityContentVisible(visible: Boolean) {
        val allowed = safety.value.ageGateStatus == AgeGateStatus.Adult && safety.value.hasAcceptedCurrentTerms
        updateSafety(safety.value.copy(communityContentVisible = visible && allowed))
    }

    override suspend fun blockUser(stationId: StationId, displayName: String) {
        val boundedName = displayName.trim().take(MAX_REPORTED_USER_CHARACTERS)
        if (boundedName.isEmpty()) return
        val identity = boundedName.normalizedCommunityIdentity()
        val retained = safety.value.blockedUsers.filterNot {
            it.stationId == stationId && it.normalizedIdentity == identity
        }
        updateSafety(
            safety.value.copy(
                blockedUsers = (retained + BlockedCommunityUser(stationId, boundedName, identity))
                    .sortedWith(compareBy({ it.stationId.value }, { it.displayName.lowercase() })),
            ),
        )
    }

    override suspend fun unblockUser(stationId: StationId, displayName: String) {
        val identity = displayName.normalizedCommunityIdentity()
        updateSafety(
            safety.value.copy(
                blockedUsers = safety.value.blockedUsers.filterNot {
                    it.stationId == stationId && it.normalizedIdentity == identity
                },
            ),
        )
    }

    override suspend fun beginReport(stationId: StationId, target: AbuseReportTarget) {
        val boundedTarget = target.bounded()
        challenge = null
        report.value = AbuseReportState(stationId, boundedTarget, AbuseReportStatus.LoadingForm)
        runCatching { remote.loadChallenge(stationId) }
            .onSuccess { loaded ->
                challenge = loaded
                report.value = report.value.copy(
                    status = AbuseReportStatus.Ready,
                    captchaImageUrl = loaded.captchaImageUrl,
                    errorMessage = null,
                    retryAllowed = false,
                )
            }
            .onFailure {
                report.value = report.value.copy(
                    status = AbuseReportStatus.Error,
                    errorMessage = it.publicReportError("The station report form could not be loaded."),
                    retryAllowed = true,
                )
            }
    }

    override suspend fun submitReport(submission: AbuseReportSubmission) {
        val currentReport = report.value
        val currentChallenge = challenge
        val target = currentReport.target
        val stationId = currentReport.stationId
        if (
            currentReport.status != AbuseReportStatus.Ready || currentChallenge == null ||
            target == null || stationId == null
        ) return

        val validated = runCatching(submission::validated).getOrElse {
            report.value = currentReport.copy(errorMessage = it.publicReportError("Check the report fields and try again."))
            return
        }
        report.value = currentReport.copy(status = AbuseReportStatus.Submitting, errorMessage = null)
        runCatching { remote.submit(stationId, currentChallenge, target, validated) }
            .onSuccess {
                challenge = null
                report.value = report.value.copy(
                    status = AbuseReportStatus.Submitted,
                    captchaImageUrl = null,
                    errorMessage = null,
                    retryAllowed = false,
                )
            }
            .onFailure {
                challenge = null
                report.value = report.value.copy(
                    status = AbuseReportStatus.Error,
                    captchaImageUrl = null,
                    errorMessage = it.publicReportError(
                        "The station did not accept the report.",
                    ),
                    retryAllowed = it !is ReportConfirmationUnknownException,
                )
            }
    }

    override fun dismissReport() {
        challenge = null
        report.value = AbuseReportState()
    }

    private fun updateSafety(updated: CommunitySafetyState) {
        safety.value = updated
        preferences.edit()
            .putString(KEY_AGE_GATE_STATUS, updated.ageGateStatus.name)
            .apply {
                if (updated.acceptedTermsVersion == null) remove(KEY_TERMS_VERSION)
                else putString(KEY_TERMS_VERSION, updated.acceptedTermsVersion)
            }
            .putBoolean(KEY_COMMUNITY_VISIBLE, updated.communityContentVisible)
            .putStringSet(KEY_BLOCKED_USERS, updated.blockedUsers.map(::encodeBlockedUser).toSet())
            .apply()
    }

    private fun readSafety(): CommunitySafetyState {
        val ageStatus = preferences.getString(KEY_AGE_GATE_STATUS, null)
            ?.let { stored -> AgeGateStatus.entries.firstOrNull { it.name == stored } }
            ?: AgeGateStatus.NotCompleted
        val acceptedVersion = preferences.getString(KEY_TERMS_VERSION, null)
        val visibilityAllowed = ageStatus == AgeGateStatus.Adult && acceptedVersion == CURRENT_COMMUNITY_TERMS_VERSION
        return CommunitySafetyState(
            ageGateStatus = ageStatus,
            acceptedTermsVersion = acceptedVersion,
            communityContentVisible = preferences.getBoolean(KEY_COMMUNITY_VISIBLE, false) && visibilityAllowed,
            blockedUsers = preferences.getStringSet(KEY_BLOCKED_USERS, emptySet()).orEmpty()
                .mapNotNull(::decodeBlockedUser)
                .distinctBy { it.stationId to it.normalizedIdentity }
                .sortedWith(compareBy({ it.stationId.value }, { it.displayName.lowercase() })),
        )
    }

    private fun encodeBlockedUser(user: BlockedCommunityUser): String =
        "${encodePreference(user.stationId.value)}|${encodePreference(user.displayName)}"

    private fun decodeBlockedUser(value: String): BlockedCommunityUser? {
        val (station, displayName) = value.split('|', limit = 2).takeIf { it.size == 2 } ?: return null
        return runCatching {
            BlockedCommunityUser(StationId(decodePreference(station)), decodePreference(displayName))
        }.getOrNull()?.takeIf { it.stationId.value.isNotBlank() && it.displayName.isNotBlank() }
    }

    private fun encodePreference(value: String) = URLEncoder.encode(value, StandardCharsets.UTF_8.name())

    private fun decodePreference(value: String) = URLDecoder.decode(value, StandardCharsets.UTF_8.name())

    private companion object {
        const val PREFERENCES_NAME = "community_safety"
        const val KEY_AGE_GATE_STATUS = "age_gate_status"
        const val KEY_TERMS_VERSION = "terms_version"
        const val KEY_COMMUNITY_VISIBLE = "community_content_visible"
        const val KEY_BLOCKED_USERS = "blocked_users"
        const val MAX_REPORTED_USER_CHARACTERS = 80
    }
}

internal data class ContactReportChallenge(
    val actionUrl: String,
    val captchaImageUrl: String,
    val challengeToken: String,
    val cookies: CookieManager,
)

internal interface AbuseReportRemoteDataSource {
    suspend fun loadChallenge(stationId: StationId): ContactReportChallenge

    suspend fun submit(
        stationId: StationId,
        challenge: ContactReportChallenge,
        target: AbuseReportTarget,
        submission: AbuseReportSubmission,
    )
}

internal class StationContactAbuseReportRemoteDataSource(
    private val resultParser: ContactReportResultParser = ContactReportResultParser(),
) : AbuseReportRemoteDataSource {
    override suspend fun loadChallenge(stationId: StationId): ContactReportChallenge = withContext(Dispatchers.IO) {
        val origin = URI(origin(stationId))
        val pageUrl = origin.resolve(CONTACT_PATH)
        val cookies = CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        val html = request(pageUrl, "GET", null, cookies, origin)
        val document = Jsoup.parse(html, pageUrl.toString())
        val forms = document.select("form").filter { form ->
            form.selectFirst("textarea[name=message]") != null &&
                form.selectFirst("input[name=opi][value=ds]") != null &&
                form.selectFirst("input[name=random_num]") != null
        }
        if (forms.size != 1) throw IOException("The station report form was not recognized.")
        val form = forms.single()
        val action = pageUrl.resolve(form.attr("action").ifBlank { CONTACT_PATH })
        requireSameOrigin(action, origin)
        form.select("select[name=subject] option[value=6]")
            .singleOrNull()
            ?.text()
            ?.takeIf { it.contains("moderator", ignoreCase = true) }
            ?: throw IOException("The station moderator destination was not available.")
        val token = form.select("input[name=random_num]").singleOrNull()?.attr("value")
            ?.takeIf { it.matches(Regex("\\d{1,12}")) }
            ?: throw IOException("The station security challenge was not recognized.")
        val captcha = form.select("img[alt=Security Code]")
            .mapNotNull { image -> image.absUrl("src").takeIf { it.contains("gfx_little") } }
            .singleOrNull()
            ?.let(::URI)
            ?: throw IOException("The station security image was not available.")
        requireSameOrigin(captcha, origin)
        ContactReportChallenge(action.toString(), captcha.toString(), token, cookies)
    }

    override suspend fun submit(
        stationId: StationId,
        challenge: ContactReportChallenge,
        target: AbuseReportTarget,
        submission: AbuseReportSubmission,
    ) = withContext(Dispatchers.IO) {
        val origin = URI(origin(stationId))
        val action = URI(challenge.actionUrl)
        requireSameOrigin(action, origin)
        val body = listOf(
            "sender_name" to submission.reporterName,
            "sender_email" to submission.reporterEmail,
            "subject" to MODERATOR_SUBJECT_VALUE,
            "message" to reportMessage(stationId, target, submission),
            "opi" to "ds",
            "gfx_check" to submission.securityCode,
            "random_num" to challenge.challengeToken,
            "submit" to "Send",
        ).joinToString("&") { (name, value) -> "${encode(name)}=${encode(value.toLegacyFormText())}" }
        resultParser.requireConfirmed(request(action, "POST", body, challenge.cookies, origin))
    }

    private fun request(
        uri: URI,
        method: String,
        body: String?,
        cookies: CookieManager,
        origin: URI,
    ): String {
        requireSameOrigin(uri, origin)
        val connection = uri.toURL().openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = REQUEST_TIMEOUT_MILLIS
            connection.readTimeout = REQUEST_TIMEOUT_MILLIS
            connection.instanceFollowRedirects = false
            connection.requestMethod = method
            connection.setRequestProperty("Accept", "text/html")
            connection.setRequestProperty("User-Agent", USER_AGENT)
            cookies.get(uri, emptyMap()).forEach { (name, values) ->
                connection.setRequestProperty(name, values.joinToString("; "))
            }
            body?.let { encoded ->
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded; charset=ISO-8859-1",
                )
                connection.outputStream.use { it.write(encoded.toByteArray(StandardCharsets.ISO_8859_1)) }
            }
            val status = connection.responseCode
            cookies.put(uri, connection.headerFields.filterKeys { it != null })
            if (status in 300..399) throw IOException("Unexpected station redirect.")
            if (status !in 200..299) throw IOException("Station returned HTTP $status.")
            connection.inputStream.bufferedReader(StandardCharsets.ISO_8859_1).use { reader ->
                reader.readBounded(MAX_RESPONSE_CHARACTERS)
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun reportMessage(
        stationId: StationId,
        target: AbuseReportTarget,
        submission: AbuseReportSubmission,
    ): String = buildString {
        appendLine("[24Seven.FM Player abuse report]")
        appendLine("Report type: ${target.kind.label}")
        appendLine("Category: ${submission.category.label}")
        appendLine("Station: ${STATION_NAMES.getValue(stationId)}")
        appendLine("Source: ${target.source.label}")
        appendLine("Reported username: ${target.reportedUser}")
        target.displayedTimestamp?.let { appendLine("Displayed timestamp: $it") }
        target.contentSnapshot?.let { appendLine("Content snapshot: $it") }
        if (submission.optionalDetails.isNotBlank()) {
            appendLine("Optional details: ${submission.optionalDetails}")
        }
        append("Submitted through the authorized in-app reporting flow.")
    }.take(MAX_REPORT_MESSAGE_CHARACTERS)

    private fun requireSameOrigin(uri: URI, origin: URI) {
        if (
            uri.scheme != "https" || !uri.host.equals(origin.host, true) ||
            effectivePort(uri) != effectivePort(origin)
        ) throw IOException("Untrusted station report destination.")
    }

    private fun effectivePort(uri: URI) = if (uri.port == -1) 443 else uri.port

    private fun origin(stationId: StationId): String = ORIGINS[stationId]
        ?: throw IOException("Unsupported station.")

    private fun encode(value: String) = URLEncoder.encode(value, StandardCharsets.ISO_8859_1.name())

    private fun String.toLegacyFormText(): String = map { character ->
        when {
            character == '<' -> '‹'
            character == '>' -> '›'
            character.code in 32..126 || character.code in 160..255 || character == '\n' -> character
            else -> '?'
        }
    }.joinToString("")

    private companion object {
        const val CONTACT_PATH = "/modules.php?name=Contact_Us"
        const val MODERATOR_SUBJECT_VALUE = "6"
        const val USER_AGENT = "24Seven.FM-Player/0.1 (Android; unofficial non-commercial client)"
        const val REQUEST_TIMEOUT_MILLIS = 10_000
        const val MAX_RESPONSE_CHARACTERS = 256_000
        const val MAX_REPORT_MESSAGE_CHARACTERS = 2_000
        val ORIGINS = mapOf(
            StationId("sst") to "https://streamingsoundtracks.com/",
            StationId("1980s") to "https://1980s.fm/",
            StationId("adagio") to "https://adagio.fm/",
            StationId("death") to "https://death.fm/",
            StationId("entranced") to "https://entranced.fm/",
        )
        val STATION_NAMES = mapOf(
            StationId("sst") to "StreamingSoundtracks.com",
            StationId("1980s") to "1980s.FM",
            StationId("adagio") to "Adagio.FM",
            StationId("death") to "Death.FM",
            StationId("entranced") to "Entranced.FM",
        )
    }
}

internal class ContactReportResultParser {
    fun requireConfirmed(html: String) {
        val document = Jsoup.parse(html)
        val visibleText = document.text().replace(Regex("\\s+"), " ")
        if (SUCCESS_MARKERS.any { visibleText.contains(it, ignoreCase = true) }) return

        val contactFormReturned = document.select("form").any { form ->
            form.selectFirst("input[name=sender_name]") != null &&
                form.selectFirst("input[name=sender_email]") != null &&
                form.selectFirst("textarea[name=message]") != null &&
                form.selectFirst("input[name=random_num]") != null
        }
        if (contactFormReturned) {
            throw ReportSubmissionRejectedException()
        }
        throw ReportConfirmationUnknownException()
    }

    private companion object {
        val SUCCESS_MARKERS = listOf(
            "thank you for contacting",
            "thank you for your message",
            "message has been sent",
            "message was sent",
            "message sent successfully",
            "email has been sent",
        )
    }
}

internal class ReportSubmissionRejectedException : IOException(
    "The security code or required report fields were rejected.",
)

internal class ReportConfirmationUnknownException : IOException(
    "The station response could not confirm delivery. Do not resend until an administrator checks receipt.",
)

private fun AbuseReportTarget.bounded() = copy(
    reportedUser = reportedUser.trim().take(80),
    displayedTimestamp = displayedTimestamp?.trim()?.take(40)?.takeIf(String::isNotEmpty),
    contentSnapshot = contentSnapshot?.trim()?.replace(Regex("\\s+"), " ")?.take(500)?.takeIf(String::isNotEmpty),
)

private fun AbuseReportSubmission.validated(): AbuseReportSubmission {
    val name = reporterName.trim().take(100)
    val email = reporterEmail.trim().take(254)
    val details = optionalDetails.trim().replace(Regex("\\s+"), " ").take(500)
    val code = securityCode.trim()
    require(name.length >= 2) { "Enter your name or station nickname." }
    require(email.matches(Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))) { "Enter a valid email address." }
    require(code.matches(Regex("[A-Za-z0-9]{3}"))) { "Enter the three-character security code." }
    return copy(reporterName = name, reporterEmail = email, optionalDetails = details, securityCode = code)
}

private fun Throwable.publicReportError(fallback: String): String = when (this) {
    is IllegalArgumentException,
    is ReportSubmissionRejectedException,
    is ReportConfirmationUnknownException,
    -> message?.takeIf(String::isNotBlank) ?: fallback
    else -> fallback
}
