package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Synthetic Stack Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
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
