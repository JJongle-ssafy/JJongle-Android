package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.entity.BgmGroup

/**
 * route To Bgm Group는 메인에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
fun routeToBgmGroup(route: String?): BgmGroup? =
    route?.let { currentRoute ->
        appRoutes.firstOrNull { it.matches(currentRoute) }?.bgmGroup
    }

private fun AppRoute.matches(currentRoute: String): Boolean {
    val staticPrefix = path.substringBefore("/{")
    return currentRoute == path || (staticPrefix != path && currentRoute.startsWith("$staticPrefix/"))
}
