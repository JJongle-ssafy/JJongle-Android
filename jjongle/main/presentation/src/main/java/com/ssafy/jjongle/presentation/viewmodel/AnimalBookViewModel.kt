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

sealed interface AnimalBookIntent : MviIntent {
    data object LoadHistories : AnimalBookIntent
    data class SelectAnimal(val animal: AnimalType) : AnimalBookIntent
    data object CloseDetail : AnimalBookIntent
}

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
