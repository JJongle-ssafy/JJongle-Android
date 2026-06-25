package com.ssafy.jjongle.presentation.navigation

import androidx.compose.runtime.Composable
import com.ssafy.jjongle.common.entity.BgmGroup

/**
 * App Route는 메인 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
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
 * App Route Navigator는 메인 흐름에서 사용하는 타입입니다.
 *
 * 호출부가 구현 세부보다 역할이 드러나는 타입에 의존하도록 분리합니다.
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
