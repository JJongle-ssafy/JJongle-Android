package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.NavSignal
import com.ssafy.jjongle.common.domain.navigation.Page
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App Navigation Helper는 여러 계층에서 반복되는 작업을 추상화한 helper 계약입니다.
 *
 * 호출자는 Android framework나 Compose 구현을 직접 알지 않고 필요한 동작만 요청합니다.
 */
@Singleton
class AppNavigationHelper @Inject constructor() : NavigationHelper {
    private val signals = MutableSharedFlow<NavSignal>(extraBufferCapacity = 64)

    override val navigationFlow: Flow<NavSignal> = signals.asSharedFlow()

    override fun navigateTo(page: Page) {
        navigateByRoute(page.toRoute())
    }

    override fun navigateByRoute(route: NavRoute) {
        signals.tryEmit(NavSignal.GoToDestPage(route))
    }

    override fun navigateDeepLink(route: NavRoute) {
        signals.tryEmit(NavSignal.DeepLink(route))
    }

    override fun navigateToBack() {
        signals.tryEmit(NavSignal.Back)
    }
}
