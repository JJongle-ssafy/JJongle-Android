package com.ssafy.jjongle.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Warm Deep Link Policy Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
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
