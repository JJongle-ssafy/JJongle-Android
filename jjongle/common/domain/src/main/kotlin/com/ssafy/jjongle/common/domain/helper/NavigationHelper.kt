package com.ssafy.jjongle.common.domain.helper

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.NavSignal
import com.ssafy.jjongle.common.domain.navigation.Page
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * NavigationHelper Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: common/domain
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
 */
interface NavigationHelper {
    /**
     * navigation host가 구독하는 이동 신호 스트림입니다.
     */
    val navigationFlow: Flow<NavSignal>

    /**
     * feature/domain의 [Page] 목적지로 이동합니다.
     */
    fun navigateTo(page: Page)

    /**
     * 이미 만들어진 [NavRoute]로 이동합니다.
     */
    fun navigateByRoute(route: NavRoute)

    /**
     * 웜 딥링크 진입을 navigation host에 전달합니다.
     */
    fun navigateDeepLink(route: NavRoute)

    /**
     * 현재 화면에서 뒤로가기를 요청합니다.
     */
    fun navigateToBack()

    /**
     * 테스트나 preview처럼 실제 navigation 처리가 필요 없는 환경에서 사용하는 빈 구현체입니다.
     */
    object NoOp : NavigationHelper {
        override val navigationFlow: Flow<NavSignal> = emptyFlow()

        override fun navigateTo(page: Page) = Unit

        override fun navigateByRoute(route: NavRoute) = Unit

        override fun navigateDeepLink(route: NavRoute) = Unit

        override fun navigateToBack() = Unit
    }
}
