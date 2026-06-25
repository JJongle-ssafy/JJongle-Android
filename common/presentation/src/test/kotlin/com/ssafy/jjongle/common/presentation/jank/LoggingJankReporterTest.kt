package com.ssafy.jjongle.common.presentation.jank

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * LoggingJankReporter의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class LoggingJankReporterTest {

    @Test
    fun frozen_frame_flushes_immediately() {
        val logs = mutableListOf<String>()
        val reporter = LoggingJankReporter(logs::add)

        reporter.report(
            JankFrameData(
                pageName = "map",
                isJank = true,
                frameDurationUiNanos = LoggingJankReporter.FROZEN_FRAME_THRESHOLD_NANOS,
                frameStartNanos = 1L,
            ),
        )

        assertTrue(logs.any { it.contains("reason=FROZEN_FRAME") })
        assertTrue(logs.any { it.contains("totalFrames=1") && it.contains("jankFrames=1") })
    }

    @Test
    fun threshold_exceeded_flushes_when_jank_ratio_crosses_limit() {
        val logs = mutableListOf<String>()
        val reporter = LoggingJankReporter(logs::add)

        repeat(LoggingJankReporter.THRESHOLD_SAMPLE_FRAMES) { index ->
            reporter.report(
                JankFrameData(
                    pageName = "animal_book",
                    isJank = index < 6,
                    frameDurationUiNanos = 16_000_000L,
                    frameStartNanos = index.toLong(),
                ),
            )
        }

        assertTrue(logs.any { it.contains("reason=THRESHOLD_EXCEEDED") })
        assertTrue(logs.any { it.contains("totalFrames=120") && it.contains("jankFrames=6") })
    }
}
