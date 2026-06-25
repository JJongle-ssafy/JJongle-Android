package com.ssafy.jjongle.presentation.navigation

import androidx.compose.runtime.Composable
import com.ssafy.jjongle.common.entity.BgmGroup

/**
 * AppRoute Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: main/presentation
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
 */
data class AppRoute(
    val path: String,
    val isBottomTab: Boolean = false,
    val bgmGroup: BgmGroup? = null,
    val syntheticStack: (Map<String, String>) -> List<GenericNavKey> = { args ->
        listOf(GenericNavKey(path, args))
    },
    val render: @Composable (Map<String, String>, AppRouteNavigator) -> Unit = { _, _ -> },
)

/**
 * AppRouteNavigator Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: main/presentation
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
 */
interface AppRouteNavigator {
    /**
     * 현재 stack 위에 새 route를 추가합니다.
     */
    fun push(route: String)

    /**
     * 전체 stack을 하나의 route로 교체합니다.
     */
    fun replaceAll(route: String)

    /**
     * 한 단계 뒤로 이동합니다.
     */
    fun pop()

    /**
     * 특정 path까지 stack을 되돌립니다.
     *
     * @param inclusive true이면 대상 path도 함께 제거합니다.
     */
    fun popTo(path: String, inclusive: Boolean)

    /**
     * stack 안의 특정 path까지 정리한 뒤 다음 route를 추가합니다.
     */
    fun navigateWithinStack(path: String, inclusive: Boolean, nextRoute: String)
}
