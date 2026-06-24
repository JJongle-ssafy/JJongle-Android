package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tangram.domain.repository.TangramGameRepository
import com.ssafy.jjongle.tangram.domain.usecase.GetTangramDetailUseCase
import com.ssafy.jjongle.tangram.domain.usecase.GetTangramHistoriesUseCase
import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage
import com.ssafy.jjongle.tangram.entity.TangramHistory
import kotlinx.collections.immutable.toPersistentList
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimalBookViewModelTest {
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
    fun init_loads_histories_into_single_ui_state() = runTest {
        val viewModel = viewModel(
            histories = listOf(
                TangramHistory(stage = 2, tangramId = 10L, animal = AnimalType.TURTLE),
                TangramHistory(stage = 5, tangramId = 11L, animal = AnimalType.TURTLE),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(AnimalType.entries.size, state.slots.size)
        assertEquals(11L, state.unlockMap[AnimalType.TURTLE]?.tangramId)
        assertEquals(true, state.slots.first { it.id == AnimalType.TURTLE.name }.unlocked)
        assertNull(state.error)
    }

    @Test
    fun select_and_close_detail_intents_update_selected_state() = runTest {
        val viewModel = viewModel(
            histories = listOf(TangramHistory(stage = 1, tangramId = 10L, animal = AnimalType.RABBIT)),
            detail = TangramDetail(tangramId = 10L, animal = AnimalType.RABBIT, story = "rabbit story"),
        )
        advanceUntilIdle()

        viewModel.onIntent(AnimalBookIntent.SelectAnimal(AnimalType.RABBIT))
        advanceUntilIdle()

        val selected = viewModel.uiState.value.selected
        assertNotNull(selected)
        assertEquals(AnimalType.RABBIT, selected?.animal)
        assertEquals(10L, selected?.tangramId)
        assertEquals("rabbit story", selected?.story)

        viewModel.onIntent(AnimalBookIntent.CloseDetail)

        assertNull(viewModel.uiState.value.selected)
    }

    private fun viewModel(
        histories: List<TangramHistory> = emptyList(),
        detail: TangramDetail = TangramDetail(tangramId = 0L, animal = AnimalType.TURTLE, story = ""),
    ): AnimalBookViewModel {
        val repository = FakeRepository(histories = histories, detail = detail)
        return AnimalBookViewModel(
            getHistories = GetTangramHistoriesUseCase(
                repo = repository,
                resourceHelper = resourceHelper,
                messageHelper = MessageHelper.NoOp,
                navigationHelper = NavigationHelper.NoOp,
                ttiHelper = TTIHelper.NoOp,
            ),
            getDetail = GetTangramDetailUseCase(
                repo = repository,
                resourceHelper = resourceHelper,
                messageHelper = MessageHelper.NoOp,
                navigationHelper = NavigationHelper.NoOp,
                ttiHelper = TTIHelper.NoOp,
            ),
        )
    }

    private val resourceHelper = object : ResourceHelper {
        override fun getString(id: Int): String = id.toString()
    }

    private class FakeRepository(
        private val histories: List<TangramHistory>,
        private val detail: TangramDetail,
    ) : TangramGameRepository {
        override suspend fun getCurrentChallengeStageId(): Int = error("not used")

        override suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage =
            TangramHistoriesPage(content = histories.toPersistentList(), isEnd = true)

        override suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail = detail
    }
}
