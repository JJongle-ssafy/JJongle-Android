package com.ssafy.jjongle.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRouteRegistryPathContractTest {

    @Test
    fun app_routes_use_url_path_format_for_in_app_and_deeplink_parity() {
        appRoutes.forEach { route ->
            assertTrue(
                "AppRoute path must start with '/' so in-app navigation and deeplinks share the same path: ${route.path}",
                route.path.startsWith("/"),
            )
            assertTrue(
                "AppRoute path must not contain whitespace: ${route.path}",
                route.path.none { it.isWhitespace() },
            )
        }
    }

    @Test
    fun camera_route_builder_preserves_registered_path_template() {
        assertEquals("/camera/{animal}", AppRoutePaths.CAMERA)
        assertEquals("/camera/turtle", AppRoutePaths.camera("turtle"))
    }
}
