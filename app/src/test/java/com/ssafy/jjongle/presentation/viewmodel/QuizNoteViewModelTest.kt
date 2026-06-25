package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryRepository
import com.ssafy.jjongle.oxgame.domain.usecase.GetOXGameHistoriesUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.GetOXGameHistoryDetailUseCase
import com.ssafy.jjongle.oxgame.entity.OX
import com.ssafy.jjongle.oxgame.entity.OXGameHistory
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * QuizNote의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizNoteViewModelTest {
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
    fun load_page_intent_updates_notes_through_single_ui_state() = runTest {
        val viewModel = viewModel(
            page = OXGameHistoryPage(
                totalPages = 2,
                content = listOf(OXGameHistory(id = 7L, playedAt = LocalDateTime.of(2026, 6, 23, 9, 0)))
                    .toPersistentList(),
            ),
        )

        viewModel.onIntent(QuizNoteIntent.LoadPage(page = 0))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(QuizNoteUi(id = 7L, recordedAt = LocalDateTime.of(2026, 6, 23, 9, 0))), state.notes)
        assertEquals(0, state.page)
        assertEquals(true, state.hasNext)
        assertFalse(state.loading)
        assertEquals(null, state.error)
    }

    @Test
    fun open_and_close_detail_intents_update_detail_through_single_ui_state() = runTest {
        val note = OXGameWrongAnswerNote(question = "question", answer = OX.X)
        val viewModel = viewModel(detail = persistentListOf(note))

        viewModel.onIntent(QuizNoteIntent.OpenDetail(historyId = 7L))
        advanceUntilIdle()

        assertEquals(listOf(note), viewModel.uiState.value.detail)
        assertFalse(viewModel.uiState.value.loading)

        viewModel.onIntent(QuizNoteIntent.CloseDetail)

        assertEquals(emptyList<OXGameWrongAnswerNote>(), viewModel.uiState.value.detail)
    }

    private fun viewModel(
        page: OXGameHistoryPage = OXGameHistoryPage.empty,
        detail: ImmutableList<OXGameWrongAnswerNote> = persistentListOf(),
    ): QuizNoteViewModel {
        val repository = FakeRepository(page = page, detail = detail)
        return QuizNoteViewModel(
            histories = GetOXGameHistoriesUseCase(
                repo = repository,
                resourceHelper = resourceHelper,
                messageHelper = MessageHelper.NoOp,
                navigationHelper = NavigationHelper.NoOp,
                ttiHelper = TTIHelper.NoOp,
            ),
            historyDetail = GetOXGameHistoryDetailUseCase(
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
        private val page: OXGameHistoryPage,
        private val detail: ImmutableList<OXGameWrongAnswerNote>,
    ) : OXGameHistoryRepository {
        override suspend fun getHistories(page: Int): OXGameHistoryPage = this.page

        override suspend fun getHistoryDetail(historyId: Long): ImmutableList<OXGameWrongAnswerNote> = detail
    }
}
