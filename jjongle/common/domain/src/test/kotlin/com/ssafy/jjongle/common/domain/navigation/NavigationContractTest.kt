package com.ssafy.jjongle.common.domain.navigation

import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationContractTest {

    @Test
    fun page_converts_to_route_with_string_args() {
        val page = object : Page {
            override fun toRoute(): NavRoute = NavRoute(
                path = "/tangram/detail",
                args = mapOf("stageId" to "3")
            )
        }

        val route = page.toRoute()

        assertEquals("/tangram/detail", route.path)
        assertEquals(mapOf("stageId" to "3"), route.args)
    }

    @Test
    fun navigation_helper_exposes_typed_navigation_signals() = runTest {
        val helper = RecordingNavigationHelper()
        val route = NavRoute(path = "/ox/note", args = mapOf("historyId" to "10"))

        helper.navigateByRoute(route)

        assertEquals(NavSignal.GoToDestPage(route), helper.navigationFlow.first())
    }

    @Test
    fun navigation_helper_supports_page_deeplink_and_back_signals() = runTest {
        val helper = RecordingNavigationHelper()
        val page = object : Page {
            override fun toRoute(): NavRoute = NavRoute("/home")
        }
        val deepLinkRoute = NavRoute("/tangram")

        helper.navigateTo(page)
        assertEquals(NavSignal.GoToDestPage(NavRoute("/home")), helper.navigationFlow.first())

        helper.navigateDeepLink(deepLinkRoute)
        assertEquals(NavSignal.DeepLink(deepLinkRoute), helper.navigationFlow.first())

        helper.navigateToBack()
        assertEquals(NavSignal.Back, helper.navigationFlow.first())
    }

    private class RecordingNavigationHelper : NavigationHelper {
        private val signals = MutableSharedFlow<NavSignal>(replay = 1, extraBufferCapacity = 8)

        override val navigationFlow = signals

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
}
