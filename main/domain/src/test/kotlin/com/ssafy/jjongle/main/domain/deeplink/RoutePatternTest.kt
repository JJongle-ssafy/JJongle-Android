package com.ssafy.jjongle.main.domain.deeplink

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Route Pattern Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class RoutePatternTest {

    @Test
    fun match_returns_path_args_for_dynamic_segments() {
        val pattern = RoutePattern("/camera/{animal}")

        assertEquals(mapOf("animal" to "turtle"), pattern.match("/camera/turtle"))
    }

    @Test
    fun match_rejects_different_segment_count_or_static_segment() {
        val pattern = RoutePattern("/camera/{animal}")

        assertNull(pattern.match("/camera"))
        assertNull(pattern.match("/camera/turtle/detail"))
        assertNull(pattern.match("/animal/turtle"))
    }

    @Test
    fun match_route_returns_first_matching_pattern() {
        val match = matchRoute(
            patterns = listOf(
                RoutePattern("/map"),
                RoutePattern("/camera/{animal}"),
            ),
            path = "/camera/rabbit",
        )

        assertEquals(RoutePattern("/camera/{animal}"), match?.first)
        assertEquals(mapOf("animal" to "rabbit"), match?.second)
    }
}
