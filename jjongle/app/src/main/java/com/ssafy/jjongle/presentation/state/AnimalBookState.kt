package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.domain.entity.TangramHistory


data class AnimalSlot(
    val id: String,
    val name: String,
    val unlocked: Boolean,
    val imageRes: Int?     // 잠금이면 null
)

data class AnimalBookState(
    val isLoading: Boolean = true,
    val slots: List<AnimalSlot> = emptyList(),
    val unlockMap: Map<AnimalType, TangramHistory> = emptyMap(),
    val selected: Selected? = null,
    val error: String? = null
) {
    data class Selected(val animal: AnimalType, val tangramId: Long, val story: String? = null)
}

