package com.ssafy.jjongle.presentation.navigation

import androidx.compose.runtime.Composable
import com.ssafy.jjongle.common.entity.BgmGroup

data class AppRoute(
    val path: String,
    val isBottomTab: Boolean = false,
    val bgmGroup: BgmGroup? = null,
    val syntheticStack: (Map<String, String>) -> List<GenericNavKey> = { args ->
        listOf(GenericNavKey(path, args))
    },
    val render: @Composable (Map<String, String>, AppRouteNavigator) -> Unit = { _, _ -> },
)

interface AppRouteNavigator {
    fun push(route: String)
    fun replaceAll(route: String)
    fun pop()
    fun popTo(path: String, inclusive: Boolean)
    fun navigateWithinStack(path: String, inclusive: Boolean, nextRoute: String)
}
