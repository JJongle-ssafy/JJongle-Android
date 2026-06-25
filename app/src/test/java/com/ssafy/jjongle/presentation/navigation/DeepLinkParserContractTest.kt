package com.ssafy.jjongle.presentation.navigation

import android.net.Uri
import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.main.domain.deeplink.RoutePattern
import com.ssafy.jjongle.presentation.deeplink.resolveRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Deep Link Parser Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class DeepLinkParserContractTest {

    @Test
    fun resolves_static_path_before_dynamic_patterns() {
        val route = Uri.parse("https://www.androidarchi.com/map?from=push").resolveRoute(
            registeredPaths = appRouteByPath.keys,
            routePatterns = appRoutePatterns,
        )

        assertEquals(NavRoute("/map", mapOf("from" to "push")), route)
    }

    @Test
    fun resolves_dynamic_path_to_template_route_with_path_args() {
        val route = Uri.parse("https://www.androidarchi.com/camera/turtle?animal=query").resolveRoute(
            registeredPaths = appRouteByPath.keys,
            routePatterns = appRoutePatterns,
        )

        assertEquals(
            NavRoute("/camera/{animal}", mapOf("animal" to "turtle")),
            route,
        )
    }

    @Test
    fun returns_null_for_unregistered_route() {
        val route = Uri.parse("https://www.androidarchi.com/unknown").resolveRoute(
            registeredPaths = appRouteByPath.keys,
            routePatterns = appRoutePatterns,
        )

        assertNull(route)
    }

    @Test
    fun parser_supports_fake_dynamic_patterns_for_domain_contract_examples() {
        val route = Uri.parse("https://www.androidarchi.com/articleList/articlePage/123?articleId=query")
            .resolveRoute(
                registeredPaths = emptySet(),
                routePatterns = listOf(RoutePattern("/articleList/articlePage/{articleId}")),
            )

        assertEquals(
            NavRoute(
                path = "/articleList/articlePage/{articleId}",
                args = mapOf("articleId" to "123"),
            ),
            route,
        )
    }
}
