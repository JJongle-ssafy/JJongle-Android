package com.ssafy.jjongle.common.domain.navigation

sealed interface NavSignal {
    data class GoToDestPage(val route: NavRoute) : NavSignal
    data class DeepLink(val route: NavRoute) : NavSignal
    data object Back : NavSignal
}
