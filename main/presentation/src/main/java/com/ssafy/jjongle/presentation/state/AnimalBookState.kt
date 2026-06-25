package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramHistory
import com.ssafy.jjongle.common.presentation.mvi.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf


/**
 * AnimalSlot 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: main/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
data class AnimalSlot(
    val id: String,
    val name: String,
    val unlocked: Boolean,
    val imageRes: Int?     // 잠금이면 null
)

/**
 * AnimalBookState 화면이 구독하는 상태 모델입니다.
 *
 * - 계층: main/presentation
 * - 책임: 렌더링에 필요한 값을 한곳에 모아 UI와 상태 변경 로직을 분리합니다.
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
