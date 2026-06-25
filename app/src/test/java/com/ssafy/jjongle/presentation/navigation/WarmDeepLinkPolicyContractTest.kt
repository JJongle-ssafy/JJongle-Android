package com.ssafy.jjongle.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * WarmDeepLinkPolicy의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class WarmDeepLinkPolicyContractTest {

    @Test
    fun bring_to_front_preserves_context_and_removes_duplicate_target() {
        val stack = listOf(
            GenericNavKey(AppRoutePaths.MAP),
            GenericNavKey(AppRoutePaths.MY_PAGE),
            GenericNavKey(AppRoutePaths.CAMERA, mapOf("animal" to "turtle")),
            GenericNavKey(AppRoutePaths.SETTING),
        )

        val result = stack.bringToFront(
            GenericNavKey(AppRoutePaths.CAMERA, mapOf("animal" to "turtle")),
        )

        assertEquals(
            listOf(
                GenericNavKey(AppRoutePaths.MAP),
                GenericNavKey(AppRoutePaths.MY_PAGE),
                GenericNavKey(AppRoutePaths.SETTING),
                GenericNavKey(AppRoutePaths.CAMERA, mapOf("animal" to "turtle")),
            ),
            result,
        )
    }

    @Test
    fun bring_to_front_appends_missing_target() {
        val stack = listOf(GenericNavKey(AppRoutePaths.MAP))

        assertEquals(
            listOf(GenericNavKey(AppRoutePaths.MAP), GenericNavKey(AppRoutePaths.QUIZ_NOTE)),
            stack.bringToFront(GenericNavKey(AppRoutePaths.QUIZ_NOTE)),
        )
    }

    @Test
    fun bring_to_front_uses_path_and_args_as_identity() {
        val stack = listOf(
            GenericNavKey(AppRoutePaths.CAMERA, mapOf("animal" to "turtle")),
            GenericNavKey(AppRoutePaths.CAMERA, mapOf("animal" to "rabbit")),
        )

        val result = stack.bringToFront(
            GenericNavKey(AppRoutePaths.CAMERA, mapOf("animal" to "turtle")),
        )

        assertEquals(
            listOf(
                GenericNavKey(AppRoutePaths.CAMERA, mapOf("animal" to "rabbit")),
                GenericNavKey(AppRoutePaths.CAMERA, mapOf("animal" to "turtle")),
            ),
            result,
        )
    }
}
