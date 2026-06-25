package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramHistory
import com.ssafy.jjongle.common.presentation.mvi.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf


/**
 * Animal Slot는 메인 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
 */
data class AnimalSlot(
    val id: String,
    val name: String,
    val unlocked: Boolean,
    val imageRes: Int?     // 잠금이면 null
)

/**
 * 메인 기능 화면을 렌더링하는 데 필요한 값을 담는 상태 스냅샷입니다.
 *
 * 여러 값을 화면에서 따로 수집하지 않도록 한 모델로 묶어, 상태 변경 지점을 ViewModel 안에서 추적할 수 있게 합니다.
 */
data class AnimalBookState(
    val isLoading: Boolean = true,
    val slots: ImmutableList<AnimalSlot> = persistentListOf(),
    val unlockMap: ImmutableMap<AnimalType, TangramHistory> = persistentMapOf(),
    val selected: Selected? = null,
    val error: String? = null
) : UiState {
    data class Selected(val animal: AnimalType, val tangramId: Long, val story: String? = null)

    companion object {
        val empty = AnimalBookState()
    }
}
