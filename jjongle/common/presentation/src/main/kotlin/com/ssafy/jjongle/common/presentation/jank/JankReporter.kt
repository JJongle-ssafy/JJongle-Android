package com.ssafy.jjongle.common.presentation.jank

import com.ssafy.jjongle.common.presentation.diagnostics.PerformanceLogger

/**
 * JankFrameData 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: common/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
data class JankFrameData(
    val pageName: String,
    val isJank: Boolean,
    val frameDurationUiNanos: Long,
    val frameStartNanos: Long,
)

/**
 * JankReportReason 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: common/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
enum class JankReportReason {
    PAGE_EXIT,
    SCROLL_END,
    FROZEN_FRAME,
    THRESHOLD_EXCEEDED,
}

/**
 * JankReporter 모듈 기능을 표현하는 interface 선언입니다.
 *
 * - 계층: common/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
interface JankReporter {
    fun report(frameData: JankFrameData)

    fun flush(reason: JankReportReason, pageName: String)
}

/**
 * LoggingJankReporter 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: common/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
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
 * DebugJankReporter 모듈 기능을 표현하는 object 선언입니다.
 *
 * - 계층: common/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
object DebugJankReporter : JankReporter by LoggingJankReporter(
    logger = { message -> PerformanceLogger.NoOp.log("JankStats", message) },
)
