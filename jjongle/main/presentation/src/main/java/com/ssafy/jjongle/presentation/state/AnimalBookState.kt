package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramHistory
import com.ssafy.jjongle.common.presentation.mvi.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf


data class AnimalSlot(
    val id: String,
    val name: String,
    val unlocked: Boolean,
    val imageRes: Int?     // 잠금이면 null
)

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
