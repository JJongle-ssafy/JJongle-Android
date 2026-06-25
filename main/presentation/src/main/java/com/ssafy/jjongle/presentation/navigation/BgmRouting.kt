package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.entity.BgmGroup

/**
 * routeToBgmGroup Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: main/presentation
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
 */
fun routeToBgmGroup(route: String?): BgmGroup? =
    route?.let { currentRoute ->
        appRoutes.firstOrNull { it.matches(currentRoute) }?.bgmGroup
    }

private fun AppRoute.matches(currentRoute: String): Boolean {
    val staticPrefix = path.substringBefore("/{")
    return currentRoute == path || (staticPrefix != path && currentRoute.startsWith("$staticPrefix/"))
}
