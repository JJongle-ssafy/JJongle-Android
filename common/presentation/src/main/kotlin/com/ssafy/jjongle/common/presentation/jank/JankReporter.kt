package com.ssafy.jjongle.common.presentation.jank

import com.ssafy.jjongle.common.presentation.diagnostics.PerformanceLogger

/**
 * Jank Frame Data는 공통 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
 */
data class JankFrameData(
    val pageName: String,
    val isJank: Boolean,
    val frameDurationUiNanos: Long,
    val frameStartNanos: Long,
)

/**
 * Jank Report Reason는 공통 흐름에서 허용되는 상태나 이벤트 종류를 제한합니다.
 *
 * 문자열이나 숫자 상수 대신 타입 분기로 처리해 잘못된 값이 전달되는 일을 줄입니다.
 */
enum class JankReportReason {
    PAGE_EXIT,
    SCROLL_END,
    FROZEN_FRAME,
    THRESHOLD_EXCEEDED,
}

/**
 * 화면별 프레임 지연 정보를 수집해 진단용 이벤트로 기록하는 jank 리포터 계약입니다.
 *
 * UI 코드는 구체적인 로깅 방식 대신 이 계약에 의존해 성능 관찰 지점을 교체할 수 있습니다.
 */
interface JankReporter {
    fun report(frameData: JankFrameData)

    fun flush(reason: JankReportReason, pageName: String)
}

/**
 * Logging Jank Reporter는 공통 흐름에서 사용하는 타입입니다.
 *
 * 호출부가 구현 세부보다 역할이 드러나는 타입에 의존하도록 분리합니다.
 */
class LoggingJankReporter(
    private val logger: (String) -> Unit,
) : JankReporter {
    private val pageStats = mutableMapOf<String, PageJankStats>()

    override fun report(frameData: JankFrameData) {
        val stats = pageStats.getOrPut(frameData.pageName) { PageJankStats() }
        stats.totalFrames += 1
        if (frameData.isJank) {
            stats.jankFrames += 1
            logger(
                "Jank frame page=${frameData.pageName} durationNs=${frameData.frameDurationUiNanos}",
            )
        }
        if (frameData.frameDurationUiNanos >= FROZEN_FRAME_THRESHOLD_NANOS) {
            flush(JankReportReason.FROZEN_FRAME, frameData.pageName)
            return
        }
        if (
            stats.totalFrames >= THRESHOLD_SAMPLE_FRAMES &&
            stats.jankFrames.toDouble() / stats.totalFrames >= JANK_RATIO_THRESHOLD
        ) {
            flush(JankReportReason.THRESHOLD_EXCEEDED, frameData.pageName)
        }
    }

    override fun flush(reason: JankReportReason, pageName: String) {
        val stats = pageStats.remove(pageName) ?: PageJankStats()
        logger(
            "Flush jank report page=$pageName reason=$reason " +
                "totalFrames=${stats.totalFrames} jankFrames=${stats.jankFrames}",
        )
    }

    private data class PageJankStats(
        var totalFrames: Int = 0,
        var jankFrames: Int = 0,
    )

    companion object {
        const val FROZEN_FRAME_THRESHOLD_NANOS: Long = 700_000_000L
        const val THRESHOLD_SAMPLE_FRAMES: Int = 120
        const val JANK_RATIO_THRESHOLD: Double = 0.05
    }
}

/**
 * Debug Jank Reporter는 공통 흐름에서 허용되는 상태나 이벤트 종류를 제한합니다.
 *
 * 문자열이나 숫자 상수 대신 타입 분기로 처리해 잘못된 값이 전달되는 일을 줄입니다.
 */
object DebugJankReporter : JankReporter by LoggingJankReporter(
    logger = { message -> PerformanceLogger.NoOp.log("JankStats", message) },
)
