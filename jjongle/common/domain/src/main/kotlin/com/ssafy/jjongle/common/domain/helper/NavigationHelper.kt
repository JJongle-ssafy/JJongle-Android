package com.ssafy.jjongle.common.domain.helper

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.NavSignal
import com.ssafy.jjongle.common.domain.navigation.Page
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface NavigationHelper {
    val navigationFlow: Flow<NavSignal>

    fun navigateTo(page: Page)

    fun navigateByRoute(route: NavRoute)

    fun navigateDeepLink(route: NavRoute)

    fun navigateToBack()

    object NoOp : NavigationHelper {
        override val navigationFlow: Flow<NavSignal> = emptyFlow()

        override fun navigateTo(page: Page) = Unit

        override fun navigateByRoute(route: NavRoute) = Unit

        override fun navigateDeepLink(route: NavRoute) = Unit

        override fun navigateToBack() = Unit
    }
}
