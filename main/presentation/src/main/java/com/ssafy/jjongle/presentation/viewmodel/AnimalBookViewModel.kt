package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.tangram.domain.usecase.GetTangramDetailUseCase
import com.ssafy.jjongle.tangram.domain.usecase.GetTangramHistoriesUseCase
import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramHistory
import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent
import com.ssafy.jjongle.presentation.state.AnimalBookState
import com.ssafy.jjongle.presentation.state.AnimalSlot
import com.ssafy.jjongle.presentation.ui.mapper.toImageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 메인 기능 화면에서 ViewModel로 전달되는 사용자 입력과 화면 이벤트입니다.
 *
 * 버튼 클릭, 화면 진입, 선택 변경 같은 입력을 타입으로 분리해 상태 변경의 시작점을 명확히 남깁니다.
 */
sealed interface AnimalBookIntent : MviIntent {
    data object LoadHistories : AnimalBookIntent
    data class SelectAnimal(val animal: AnimalType) : AnimalBookIntent
    data object CloseDetail : AnimalBookIntent
}

/**
 * Animal Book Reducer Event는 메인 진행 중 발생한 도메인 이벤트입니다.
 *
 * 이벤트 종류를 타입으로 나눠 ViewModel이나 엔진이 문자열 분기 없이 게임 흐름을 처리하게 합니다.
 */
sealed interface AnimalBookReducerEvent : ReducerEvent {
    data object LoadingStarted : AnimalBookReducerEvent
    data class HistoriesLoaded(
        val slots: ImmutableList<AnimalSlot>,
        val unlockMap: ImmutableMap<AnimalType, TangramHistory>,
    ) : AnimalBookReducerEvent

    data class DetailSelected(val selected: AnimalBookState.Selected) : AnimalBookReducerEvent
    data class DetailStoryLoaded(val story: String) : AnimalBookReducerEvent
    data object DetailClosed : AnimalBookReducerEvent
    data class Failed(val message: String?) : AnimalBookReducerEvent
}

/**
 * Animal Book 화면의 사용자 입력과 비동기 결과를 UI 상태로 변환하는 ViewModel입니다.
 *
 * UseCase 호출, 오류 처리, 상태 전이를 한곳에 모아 Compose 화면은 상태 구독과 Intent 전달에 집중하도록 합니다.
 */
@HiltViewModel
class AnimalBookViewModel @Inject constructor(
    private val getHistories: GetTangramHistoriesUseCase,
    private val getDetail: GetTangramDetailUseCase
) : MviViewModel<AnimalBookIntent, AnimalBookState, AnimalBookReducerEvent>(
    initialState = AnimalBookState.empty,
) {

    init {
        onIntent(AnimalBookIntent.LoadHistories)
    }

    override fun onIntent(intent: AnimalBookIntent) {
        when (intent) {
            AnimalBookIntent.LoadHistories -> load()
            is AnimalBookIntent.SelectAnimal -> selectAnimal(intent.animal)
            AnimalBookIntent.CloseDetail -> dispatch(AnimalBookReducerEvent.DetailClosed)
        }
    }

    private fun load() = viewModelScope.launch {
        dispatch(AnimalBookReducerEvent.LoadingStarted)
        getHistories(page = 0, size = 200)
            .onSuccess { page ->
                val serverUnlock = page.content
                    .groupBy { it.animal }
                    .mapValues { it.value.maxBy { h -> h.stage } }

                val merged = (currentState.unlockMap + serverUnlock).toPersistentMap()

                val slots = AnimalType.values().map { type ->
                    val opened = merged.containsKey(type)
                    AnimalSlot(
                        id = type.name,
                        name = type.name,
                        unlocked = opened,
                        imageRes = if (opened) type.toImageRes() else null
                    )
                }.toPersistentList()

                dispatch(AnimalBookReducerEvent.HistoriesLoaded(slots = slots, unlockMap = merged))
            }
            .onFailure {
                dispatch(AnimalBookReducerEvent.Failed(it.message))
            }
    }


    private fun selectAnimal(animal: AnimalType) {
        val rec = currentState.unlockMap[animal] ?: return
        dispatch(AnimalBookReducerEvent.DetailSelected(AnimalBookState.Selected(animal, rec.tangramId)))

        viewModelScope.launch {
            getDetail(rec.tangramId, animal)
                .onSuccess { d ->
                    dispatch(AnimalBookReducerEvent.DetailStoryLoaded(d.story))
                }
                .onFailure { e ->
                    dispatch(AnimalBookReducerEvent.Failed(e.message ?: "알 수 없는 오류"))
                }
        }

    }

    override fun reduce(state: AnimalBookState, event: AnimalBookReducerEvent): AnimalBookState {
        return when (event) {
            AnimalBookReducerEvent.LoadingStarted -> state.copy(isLoading = true, error = null)
            is AnimalBookReducerEvent.HistoriesLoaded -> state.copy(
                isLoading = false,
                slots = event.slots,
                unlockMap = event.unlockMap,
            )

            is AnimalBookReducerEvent.DetailSelected -> state.copy(selected = event.selected, error = null)
            is AnimalBookReducerEvent.DetailStoryLoaded -> state.copy(
                selected = state.selected?.copy(story = event.story),
            )

            AnimalBookReducerEvent.DetailClosed -> state.copy(selected = null)
            is AnimalBookReducerEvent.Failed -> state.copy(isLoading = false, error = event.message)
        }
    }

}
