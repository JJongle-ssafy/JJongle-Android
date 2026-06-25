package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * SyntheticStack의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class SyntheticStackContractTest {

    @Test
    fun camera_deeplink_builds_parent_stack_before_leaf_route() {
        val stack = NavRoute(
            path = AppRoutePaths.CAMERA,
            args = mapOf("animal" to "turtle"),
        ).toSyntheticComposeStack()

        assertEquals(listOf("/animal_book", "/camera/turtle"), stack)
    }

    @Test
    fun tangram_tutorial_deeplink_builds_game_parent_stack() {
        val stack = NavRoute(AppRoutePaths.TANGRAM_TUTORIAL).toSyntheticComposeStack()

        assertEquals(
            listOf("/tangram_title", "/tangram_stage", "/tangram_tutorial"),
            stack,
        )
    }

    @Test
    fun unknown_route_falls_back_to_single_compose_route() {
        val stack = NavRoute("/unknown", mapOf("from" to "test")).toSyntheticComposeStack()

        assertEquals(listOf("/unknown?from=test"), stack)
    }
}
