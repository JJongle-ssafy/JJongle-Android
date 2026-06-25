package com.ssafy.jjongle.common.presentation.message

import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MessageEffectBus의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MessageEffectBusTest {

    @Test
    fun showToast_emitsSnackBarEffect() = runTest {
        val bus = MessageEffectBus()
        val effect = async { bus.effects.first() }
        runCurrent()

        bus.showToast("세션이 만료되었습니다.")

        assertEquals(
            MessageEffect.SnackBar("세션이 만료되었습니다."),
            effect.await(),
        )
    }

    @Test
    fun showOneButtonDialog_emitsDialogEffect() = runTest {
        val bus = MessageEffectBus()
        val effect = async { bus.effects.first() }
        runCurrent()

        bus.showOneButtonDialog(
            cantIgnore = true,
            descText = "업데이트가 필요합니다.",
        )

        val dialog = effect.await()
        assertTrue(dialog is MessageEffect.OneButtonDialog)
        dialog as MessageEffect.OneButtonDialog
        assertTrue(dialog.cantIgnore)
        assertEquals("업데이트가 필요합니다.", dialog.description)
    }
}
