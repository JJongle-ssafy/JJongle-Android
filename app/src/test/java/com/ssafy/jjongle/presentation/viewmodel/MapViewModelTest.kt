package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.domain.repository.BgmRepository
import com.ssafy.jjongle.common.domain.repository.SettingsRepository
import com.ssafy.jjongle.common.entity.BgmGroup
import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tti.TTIMetadata
import com.ssafy.jjongle.tti.TTIPage
import com.ssafy.jjongle.tti.TTITimelineCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Map의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {
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
    fun map_view_model_uses_mvi_contract() {
        assertTrue(MviViewModel::class.java.isAssignableFrom(MapViewModel::class.java))
    }

    @Test
    fun move_character_intent_updates_single_ui_state() = runTest {
        val viewModel = viewModel()

        viewModel.onIntent(MapIntent.StartWalking)
        viewModel.onIntent(MapIntent.MoveCharacterTo(x = 120f, y = 240f))
        advanceUntilIdle()

        assertEquals(120f, viewModel.uiState.value.characterX)
        assertEquals(240f, viewModel.uiState.value.characterY)
        assertFalse(viewModel.uiState.value.isWalking)
    }

    @Test
    fun toggle_bgm_intent_updates_settings_and_bgm_repository() = runTest {
        val settingsRepository = FakeSettingsRepository(initialBgmEnabled = true)
        val bgmRepository = FakeBgmRepository()
        val viewModel = viewModel(
            settingsRepository = settingsRepository,
            bgmRepository = bgmRepository,
        )

        viewModel.onIntent(MapIntent.ToggleBgm)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isBgmOn)
        assertEquals(false, settingsRepository.bgmEnabled.value)
        assertEquals(1, bgmRepository.pauseCallCount)
    }

    @Test
    fun map_view_model_marks_tti_lifecycle() = runTest {
        val ttiHelper = RecordingTTIHelper()
        val viewModel = viewModel(ttiHelper = ttiHelper)
        advanceUntilIdle()

        viewModel.onInitialContentDisplayed()
        viewModel.onPageLeaving()

        assertEquals(
            listOf(
                "start:map",
                "startTimeline:map:VIEW_BINDING",
                "endTimeline:map:VIEW_BINDING",
                "end:map",
                "shot:map",
            ),
            ttiHelper.events,
        )
    }

    private fun viewModel(
        settingsRepository: FakeSettingsRepository = FakeSettingsRepository(),
        bgmRepository: FakeBgmRepository = FakeBgmRepository(),
        ttiHelper: TTIHelper = TTIHelper.NoOp,
    ): MapViewModel = MapViewModel(
        bgmRepository = bgmRepository,
        settingsRepository = settingsRepository,
        ttiHelper = ttiHelper,
    )

    private class FakeSettingsRepository(
        initialBgmEnabled: Boolean = true,
    ) : SettingsRepository {
        val bgmEnabled = MutableStateFlow(initialBgmEnabled)

        override fun getBgmEnabled(): Flow<Boolean> = bgmEnabled

        override suspend fun setBgmEnabled(enabled: Boolean) {
            bgmEnabled.value = enabled
        }
    }

    private class FakeBgmRepository : BgmRepository {
        var playGroup: BgmGroup? = null
        var pauseCallCount: Int = 0

        override fun playFor(group: BgmGroup) {
            playGroup = group
        }

        override fun pause() {
            pauseCallCount += 1
        }

        override fun resume() = Unit

        override fun stop() = Unit
    }

    private class RecordingTTIHelper : TTIHelper {
        val events = mutableListOf<String>()

        override fun startTTITracking(page: TTIPage) {
            events += "start:${page.pageName}"
        }

        override fun startTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory) {
            events += "startTimeline:${page.pageName}:$timelineCategory"
        }

        override fun endTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory) {
            events += "endTimeline:${page.pageName}:$timelineCategory"
        }

        override fun endTTITracking(page: TTIPage) {
            events += "end:${page.pageName}"
        }

        override fun shotTTILogging(page: TTIPage) {
            events += "shot:${page.pageName}"
        }

        override fun addTTIMetaData(page: TTIPage, metadata: TTIMetadata, value: String) = Unit
    }
}
