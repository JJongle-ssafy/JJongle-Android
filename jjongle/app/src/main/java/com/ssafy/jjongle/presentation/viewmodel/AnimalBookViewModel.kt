package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.R
import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.domain.usecase.GetTangramDetailUseCase
import com.ssafy.jjongle.domain.usecase.GetTangramHistoriesUseCase
import com.ssafy.jjongle.presentation.state.AnimalBookState
import com.ssafy.jjongle.presentation.state.AnimalSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalBookViewModel @Inject constructor(
    private val getHistories: GetTangramHistoriesUseCase,
    private val getDetail: GetTangramDetailUseCase
) : ViewModel() {

    private val _ui = MutableStateFlow(AnimalBookState())
    val ui: StateFlow<AnimalBookState> = _ui.asStateFlow()

    init {
        load()
    }

    // 원래 코드
//    private fun load() = viewModelScope.launch {
//        runCatching { getHistories(page = 0, size = 200) }
//            .onSuccess { list ->
//                val unlock =
//                    list.groupBy { it.animal }.mapValues { it.value.maxBy { h -> h.stage } }
//                val slots = AnimalType.values().map { type ->
//                    val opened = unlock.containsKey(type)
//                    AnimalSlot(
//                        id = type.name,
//                        name = type.name,
//                        unlocked = opened,
//                        imageRes = if (opened) type.toImageRes() else null
//                    )
//                }
//                _ui.value = AnimalBookState(isLoading = false, slots = slots, unlockMap = unlock)
//            }
//            .onFailure { _ui.value = _ui.value.copy(isLoading = false, error = it.message) }
//    }


    // 디버그용
    private fun load() = viewModelScope.launch {
        runCatching { getHistories(page = 0, size = 200) }
            .onSuccess { list ->
                val serverUnlock = list
                    .groupBy { it.animal }
                    .mapValues { it.value.maxBy { h -> h.stage } }

                // 디버그로 미리 들어있는 값과 서버 값을 합침(서버가 덮어쓰도록 뒤에 둠)
                val merged = _ui.value.unlockMap + serverUnlock

                val slots = AnimalType.values().map { type ->
                    val opened = merged.containsKey(type)
                    AnimalSlot(
                        id = type.name,
                        name = type.name,
                        unlocked = opened,
                        imageRes = if (opened) type.toImageRes() else null
                    )
                }

                _ui.value = _ui.value.copy(
                    isLoading = false,
                    slots = slots,
                    unlockMap = merged
                )
            }
            .onFailure {
                _ui.value = _ui.value.copy(isLoading = false, error = it.message)
            }
    }


    fun onSelect(animal: AnimalType) {
        val rec = _ui.value.unlockMap[animal] ?: return
        _ui.update { it.copy(selected = AnimalBookState.Selected(animal, rec.tangramId)) }

        viewModelScope.launch {
            runCatching { getDetail(rec.tangramId, animal) }   // type 함께 전달
                .onSuccess { d ->
                    _ui.update { s -> s.copy(selected = s.selected?.copy(story = d.story)) }
                }
                .onFailure { e ->                      // 예외는 e
                    _ui.update { state ->              // 상태는 state
                        state.copy(error = e.message ?: "알 수 없는 오류")
                    }
                }
        }

    }

    fun closeDetail() = _ui.update { it.copy(selected = null) }

    private fun AnimalType.toImageRes(): Int = when (this) {
        AnimalType.TURTLE -> R.drawable.turtle
        AnimalType.RABBIT -> R.drawable.rabbit
        AnimalType.SWAN -> R.drawable.swan
        AnimalType.DOG -> R.drawable.dog
        AnimalType.DOLPHIN -> R.drawable.dolphin
        AnimalType.CRANE -> R.drawable.crane
        AnimalType.PARROT -> R.drawable.parrot
        AnimalType.BEAR -> R.drawable.bear
        AnimalType.SHEEP -> R.drawable.sheep
    }


    /** * 디버그용: 칠교 unlock 상태를 강제로 변경합니다.
     * @param ids AnimalType의 가변 인자 목록
     * - 예: debugUnlock(AnimalType.TURTLE, AnimalType.RABBIT)
     * - AnimalType의 enum 값들을 전달하여 해당 칠교를 잠금 해제합니다.
     * - TangramHistory는 stage 999로 설정되어, 실제 게임 진행과는 무관합니다.
     */

//    // import 생략 가능: AnimalType, TangramHistory만 필요
//    fun debugUnlock(vararg ids: AnimalType) {
//        val add = ids.associateWith { type ->
//            TangramHistory(stage = 999, tangramId = type.ordinal.toLong(), animal = type)
//        }
//        // ⬇️ flow.update 대신 value로 덮기 (it 문제도 같이 해결)
//        _ui.value = _ui.value.copy(unlockMap = _ui.value.unlockMap + add)
//    }
//
//    fun debugClearUnlock() {
//        _ui.value = _ui.value.copy(unlockMap = emptyMap())
//    }

}





