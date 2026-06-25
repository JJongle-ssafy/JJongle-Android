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
 * Quiz Note Ui는 메인 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
 */
data class QuizNoteUi(val id: Long, val recordedAt: LocalDateTime)

/**
 * 메인 기능 화면을 렌더링하는 데 필요한 값을 담는 상태 스냅샷입니다.
 *
 * 여러 값을 화면에서 따로 수집하지 않도록 한 모델로 묶어, 상태 변경 지점을 ViewModel 안에서 추적할 수 있게 합니다.
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
 * 메인 기능 화면에서 ViewModel로 전달되는 사용자 입력과 화면 이벤트입니다.
 *
 * 버튼 클릭, 화면 진입, 선택 변경 같은 입력을 타입으로 분리해 상태 변경의 시작점을 명확히 남깁니다.
 */
sealed interface QuizNoteIntent : MviIntent {
    data class LoadPage(val page: Int) : QuizNoteIntent
    data class OpenDetail(val historyId: Long) : QuizNoteIntent
    data object CloseDetail : QuizNoteIntent
}

/**
 * Quiz Note Reducer Event는 메인 진행 중 발생한 도메인 이벤트입니다.
 *
 * 이벤트 종류를 타입으로 나눠 ViewModel이나 엔진이 문자열 분기 없이 게임 흐름을 처리하게 합니다.
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
 * Quiz Note 화면의 사용자 입력과 비동기 결과를 UI 상태로 변환하는 ViewModel입니다.
 *
 * UseCase 호출, 오류 처리, 상태 전이를 한곳에 모아 Compose 화면은 상태 구독과 Intent 전달에 집중하도록 합니다.
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
