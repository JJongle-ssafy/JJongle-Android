package com.ssafy.jjongle.tti

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TTIHelper Impl Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class TTIHelperImplTest {

    @Test
    fun shotTTILogging_emits_tti_duration_timeline_and_metadata() {
        var now = 1_000L
        val logs = mutableListOf<String>()
        val helper = TTIHelperImpl(
            clockMillis = { now },
            logger = logs::add,
        )

        helper.startTTITracking(TestPage)
        helper.startTTITimeline(TestPage, TTITimelineCategory.VIEW_BINDING)
        now += 42L
        helper.endTTITimeline(TestPage, TTITimelineCategory.VIEW_BINDING)
        helper.addTTIMetaData(TestPage, TTIMetadata.TTI_LOG_VERSION, "custom")
        now += 8L
        helper.endTTITracking(TestPage)
        helper.shotTTILogging(TestPage)

        val log = logs.single()
        assertTrue(log.contains("Shot TTI Logging"))
        assertTrue(log.contains("page=map"))
        assertTrue(log.contains("tti.tti_time=50"))
        assertTrue(log.contains("tti.view_binding_time=42"))
        assertTrue(log.contains("tti.is_bounced=false"))
        assertTrue(log.contains("tti.is_timeout=false"))
        assertTrue(log.contains("tti.tti_log_version=custom"))
    }

    @Test
    fun shotTTILogging_marks_bounce_and_timeout_when_tracking_never_ends() {
        var now = 0L
        val logs = mutableListOf<String>()
        val helper = TTIHelperImpl(
            clockMillis = { now },
            logger = logs::add,
        )

        helper.startTTITracking(TestPage)
        now = TTIHelperImpl.TTI_TIMEOUT_MILLISECONDS
        helper.shotTTILogging(TestPage)

        val log = logs.single()
        assertTrue(log.contains("tti.tti_time=${TTIHelperImpl.TTI_TIMEOUT_MILLISECONDS}"))
        assertTrue(log.contains("tti.is_bounced=true"))
        assertTrue(log.contains("tti.is_timeout=true"))
    }

    private object TestPage : TTIPage {
        override val pageName: String = "map"
    }
}
