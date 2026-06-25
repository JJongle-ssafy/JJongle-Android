package com.ssafy.jjongle.tangram.presentation.viewmodel

import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tangram.domain.repository.TangramGameRepository
import com.ssafy.jjongle.tangram.domain.usecase.TangramGameUseCase
import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

/**
 * Tangram Stage View Model Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TangramStageViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loads_current_challenge_stage_into_single_ui_state() = runTest {
        val viewModel = TangramStageViewModel(useCase(stage = 3))

        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.currentStage)
        assertEquals(3, viewModel.uiState.value.currentChallengeStageId)
        assertEquals(440f, viewModel.uiState.value.characterX)
        assertEquals(320f, viewModel.uiState.value.characterY)
        assertFalse(viewModel.uiState.value.isCharacterMoving)
    }

    @Test
    fun move_stage_intent_updates_state_through_mvi_entrypoint() = runTest {
        val viewModel = TangramStageViewModel(useCase(stage = 3))
        advanceUntilIdle()

        viewModel.onIntent(TangramStageIntent.MoveToStage(2))
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.currentStage)
        assertEquals(590f, viewModel.uiState.value.characterX)
        assertEquals(420f, viewModel.uiState.value.characterY)
        assertFalse(viewModel.uiState.value.isCharacterMoving)
    }

    private fun useCase(stage: Int): TangramGameUseCase = TangramGameUseCase(
        tangramGameRepository = FakeRepository(stage),
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = MessageHelper.NoOp,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeRepository(
        private val stage: Int,
    ) : TangramGameRepository {
        override suspend fun getCurrentChallengeStageId(): Int = stage

        override suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage =
            TangramHistoriesPage()

        override suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail =
            error("not used")
    }
}
