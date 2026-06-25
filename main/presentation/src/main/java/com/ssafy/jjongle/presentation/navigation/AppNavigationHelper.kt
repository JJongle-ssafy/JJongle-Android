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
 * AppNavigationHelper Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: main/presentation
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
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
