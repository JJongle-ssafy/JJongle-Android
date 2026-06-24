package com.ssafy.jjongle.common.domain.navigation

data class NavRoute(
    val path: String,
    val args: Map<String, String> = emptyMap(),
)

interface Page {
    fun toRoute(): NavRoute
}
