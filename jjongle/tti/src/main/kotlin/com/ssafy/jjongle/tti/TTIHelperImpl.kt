package com.ssafy.jjongle.tti

/**
 * TTIHelperImpl TTI 계측에 필요한 값을 정의합니다.
 *
 * - 계층: tti
 * - 책임: 화면 진입부터 최초 상호작용 가능 시점까지의 성능 측정을 보조합니다.
 */
class TTIHelperImpl(
    private val clockMillis: () -> Long = System::currentTimeMillis,
    private val logger: (String) -> Unit = {},
) : TTIHelper {

    private val sessions = mutableMapOf<String, TTISession>()

    override fun startTTITracking(page: TTIPage) {
        sessions[page.pageName] = TTISession(
            pageName = page.pageName,
            startedAtMillis = clockMillis(),
        ).also { session ->
            session.metadata[TTIMetadata.PAGE_NAME] = page.pageName
            session.metadata[TTIMetadata.IS_BOUNCED] = false.toString()
            session.metadata[TTIMetadata.IS_TIMEOUT] = false.toString()
            session.metadata[TTIMetadata.TTI_LOG_VERSION] = TTI_LOG_VERSION
        }
    }

    override fun startTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory) {
        sessionFor(page).timelineStarts[timelineCategory] = clockMillis()
    }

    override fun endTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory) {
        val session = sessionFor(page)
        val startedAt = session.timelineStarts.remove(timelineCategory) ?: return
        session.timelineDurations[timelineCategory] = (clockMillis() - startedAt).coerceAtLeast(0L)
    }

    override fun endTTITracking(page: TTIPage) {
        val session = sessionFor(page)
        if (session.endedAtMillis == null) {
            session.endedAtMillis = clockMillis()
        }
    }

    override fun shotTTILogging(page: TTIPage) {
        val session = sessions.remove(page.pageName) ?: return
        val now = clockMillis()
        val endedAt = session.endedAtMillis
        val total = ((endedAt ?: now) - session.startedAtMillis).coerceAtLeast(0L)
        val timeout = endedAt == null && total >= TTI_TIMEOUT_MILLISECONDS

        session.metadata[TTIMetadata.IS_BOUNCED] = (endedAt == null).toString()
        session.metadata[TTIMetadata.IS_TIMEOUT] = timeout.toString()

        logger(buildLogLine(session, total))
    }

    override fun addTTIMetaData(page: TTIPage, metadata: TTIMetadata, value: String) {
        sessionFor(page).metadata[metadata] = value
    }

    private fun sessionFor(page: TTIPage): TTISession =
        sessions.getOrPut(page.pageName) {
            TTISession(pageName = page.pageName, startedAtMillis = clockMillis()).also { session ->
                session.metadata[TTIMetadata.PAGE_NAME] = page.pageName
                session.metadata[TTIMetadata.TTI_LOG_VERSION] = TTI_LOG_VERSION
            }
        }

    private fun buildLogLine(session: TTISession, totalMillis: Long): String {
        val timelinePart = TTITimelineCategory.entries.joinToString(separator = ", ") { category ->
            "${category.logKey}=${session.timelineDurations[category] ?: 0L}"
        }
        val metadataPart = TTIMetadata.entries.joinToString(separator = ", ") { metadata ->
            "${metadata.logKey}=${session.metadata[metadata].orEmpty()}"
        }
        return "Shot TTI Logging: page=${session.pageName}, tti.tti_time=$totalMillis, $timelinePart, $metadataPart"
    }

    private data class TTISession(
        val pageName: String,
        val startedAtMillis: Long,
        val timelineStarts: MutableMap<TTITimelineCategory, Long> = mutableMapOf(),
        val timelineDurations: MutableMap<TTITimelineCategory, Long> = mutableMapOf(),
        val metadata: MutableMap<TTIMetadata, String> = mutableMapOf(),
        var endedAtMillis: Long? = null,
    )

    companion object {
        const val TTI_TIMEOUT_MILLISECONDS = 20_000L
        private const val TTI_LOG_VERSION = "1"
    }
}

private val TTITimelineCategory.logKey: String
    get() = when (this) {
        TTITimelineCategory.API_REQUEST_READY_TIME -> "tti.api_request_ready_time"
        TTITimelineCategory.API_RESPONSE_TIME -> "tti.api_response_time"
        TTITimelineCategory.VIEW_CREATION -> "tti.view_creation_time"
        TTITimelineCategory.VIEW_BINDING -> "tti.view_binding_time"
        TTITimelineCategory.IMAGE_LOADED -> "tti.image_loaded_time"
    }

private val TTIMetadata.logKey: String
    get() = when (this) {
        TTIMetadata.PAGE_NAME -> "tti.page_name"
        TTIMetadata.IS_BOUNCED -> "tti.is_bounced"
        TTIMetadata.IS_TIMEOUT -> "tti.is_timeout"
        TTIMetadata.TTI_LOG_VERSION -> "tti.tti_log_version"
    }
