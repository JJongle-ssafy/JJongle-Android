package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.entity.BgmGroup

fun routeToBgmGroup(route: String?): BgmGroup? =
    route?.let { currentRoute ->
        appRoutes.firstOrNull { it.matches(currentRoute) }?.bgmGroup
    }

private fun AppRoute.matches(currentRoute: String): Boolean {
    val staticPrefix = path.substringBefore("/{")
    return currentRoute == path || (staticPrefix != path && currentRoute.startsWith("$staticPrefix/"))
}
