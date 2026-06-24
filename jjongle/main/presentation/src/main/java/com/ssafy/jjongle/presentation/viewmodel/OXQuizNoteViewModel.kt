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

data class QuizNoteUi(val id: Long, val recordedAt: LocalDateTime)

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

sealed interface QuizNoteIntent : MviIntent {
    data class LoadPage(val page: Int) : QuizNoteIntent
    data class OpenDetail(val historyId: Long) : QuizNoteIntent
    data object CloseDetail : QuizNoteIntent
}

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
