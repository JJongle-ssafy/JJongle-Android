package com.ssafy.jjongle.main.domain.deeplink

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

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
