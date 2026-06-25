package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.oxgame.domain.usecase.GetOXGameHistoriesUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.GetOXGameHistoryDetailUseCase
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent
import com.ssafy.jjongle.common.presentation.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * QuizNoteUi 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: main/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
data class QuizNoteUi(val id: Long, val recordedAt: LocalDateTime)

/**
 * QuizNoteState 화면이 구독하는 상태 모델입니다.
 *
 * - 계층: main/presentation
 * - 책임: 렌더링에 필요한 값을 한곳에 모아 UI와 상태 변경 로직을 분리합니다.
 */
data class QuizNoteState(
    val notes: ImmutableList<QuizNoteUi> = persistentListOf(),
    val page: Int = 0,
    val hasNext: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val detail: ImmutableList<OXGameWrongAnswerNote> = persistentListOf(),
) : UiState {
    companion object {
        val empty = QuizNoteState()
    }
}

/**
 * QuizNoteIntent 화면에서 ViewModel로 전달되는 사용자 입력을 정의합니다.
 *
 * - 계층: main/presentation
 * - 책임: UI 이벤트를 MVI intent로 분리해 상태 변경 진입점을 명확히 합니다.
 */
sealed interface QuizNoteIntent : MviIntent {
    data class LoadPage(val page: Int) : QuizNoteIntent
    data class OpenDetail(val historyId: Long) : QuizNoteIntent
    data object CloseDetail : QuizNoteIntent
}

/**
 * QuizNoteReducerEvent ViewModel 내부 상태 변경 이벤트를 정의합니다.
 *
 * - 계층: main/presentation
 * - 책임: 비동기 결과와 사용자 입력을 reducer가 처리할 수 있는 이벤트로 정리합니다.
 */
sealed interface QuizNoteReducerEvent : ReducerEvent {
    data object LoadingStarted : QuizNoteReducerEvent
    data class PageLoaded(
        val notes: ImmutableList<QuizNoteUi>,
        val page: Int,
        val hasNext: Boolean,
    ) : QuizNoteReducerEvent

    data class DetailLoaded(val detail: ImmutableList<OXGameWrongAnswerNote>) : QuizNoteReducerEvent
    data object DetailClosed : QuizNoteReducerEvent
    data class Failed(val message: String?) : QuizNoteReducerEvent
}

/**
 * QuizNoteViewModel 화면 상태와 이벤트를 처리하는 ViewModel입니다.
 *
 * - 계층: main/presentation
 * - 책임: 유스케이스를 호출하고 UI가 구독할 상태 흐름을 제공합니다.
 */
@HiltViewModel
class QuizNoteViewModel @Inject constructor(
    private val histories: GetOXGameHistoriesUseCase,
    private val historyDetail: GetOXGameHistoryDetailUseCase,
) : MviViewModel<QuizNoteIntent, QuizNoteState, QuizNoteReducerEvent>(
    initialState = QuizNoteState.empty,
) {

    override fun onIntent(intent: QuizNoteIntent) {
        when (intent) {
            is QuizNoteIntent.LoadPage -> loadPage(intent.page)
            is QuizNoteIntent.OpenDetail -> openDetail(intent.historyId)
            QuizNoteIntent.CloseDetail -> dispatch(QuizNoteReducerEvent.DetailClosed)
        }
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            dispatch(QuizNoteReducerEvent.LoadingStarted)
            histories(page)
                .onSuccess { res ->
                    val items = res.content
                        .map { h -> QuizNoteUi(h.id, h.playedAt) }
                        .toPersistentList()
                    dispatch(
                        QuizNoteReducerEvent.PageLoaded(
                            notes = items,
                            page = page,
                            hasNext = if (res.totalPages > 0) page < res.totalPages - 1 else items.size >= PAGE_SIZE,
                        ),
                    )
                }
                .onFailure { e ->
                    dispatch(QuizNoteReducerEvent.Failed(e.message))
                }
        }
    }

    private fun openDetail(historyId: Long) {
        viewModelScope.launch {
            dispatch(QuizNoteReducerEvent.LoadingStarted)
            historyDetail(historyId)
                .onSuccess { list ->
                    dispatch(QuizNoteReducerEvent.DetailLoaded(list.toPersistentList()))
                }
                .onFailure { e ->
                    dispatch(QuizNoteReducerEvent.Failed(e.message))
                }
        }
    }

    override fun reduce(state: QuizNoteState, event: QuizNoteReducerEvent): QuizNoteState {
        return when (event) {
            QuizNoteReducerEvent.LoadingStarted -> state.copy(loading = true, error = null)
            is QuizNoteReducerEvent.PageLoaded -> state.copy(
                notes = event.notes,
                page = event.page,
                hasNext = event.hasNext,
                loading = false,
            )

            is QuizNoteReducerEvent.DetailLoaded -> state.copy(
                detail = event.detail,
                loading = false,
            )

            QuizNoteReducerEvent.DetailClosed -> state.copy(detail = persistentListOf())
            is QuizNoteReducerEvent.Failed -> state.copy(loading = false, error = event.message)
        }
    }

    private companion object {
        const val PAGE_SIZE = 3
    }
}
