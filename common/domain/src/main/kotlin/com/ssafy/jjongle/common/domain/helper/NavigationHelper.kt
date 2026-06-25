package com.ssafy.jjongle.common.domain.helper

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.NavSignal
import com.ssafy.jjongle.common.domain.navigation.Page
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Navigation Helper는 여러 계층에서 반복되는 작업을 추상화한 helper 계약입니다.
 *
 * 호출자는 Android framework나 Compose 구현을 직접 알지 않고 필요한 동작만 요청합니다.
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
 * No Op는 공통 흐름에서 허용되는 상태나 이벤트 종류를 제한합니다.
 *
 * 문자열이나 숫자 상수 대신 타입 분기로 처리해 잘못된 값이 전달되는 일을 줄입니다.
 */
    object NoOp : NavigationHelper {
        override val navigationFlow: Flow<NavSignal> = emptyFlow()

        override fun navigateTo(page: Page) = Unit

        override fun navigateByRoute(route: NavRoute) = Unit

        override fun navigateDeepLink(route: NavRoute) = Unit

        override fun navigateToBack() = Unit
    }
}
