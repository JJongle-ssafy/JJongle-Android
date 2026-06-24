package com.ssafy.jjongle.tangram.entity

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TangramHistoriesPage(
    val content: ImmutableList<TangramHistory> = persistentListOf(),
    val isEnd: Boolean = true,
) {
    companion object {
        val empty = TangramHistoriesPage()
    }
}
