package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.NavSignal
import com.ssafy.jjongle.common.domain.navigation.Page
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * AppNavigationHelper의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class AppNavigationHelperTest {

    @Test
    fun navigate_to_page_emits_destination_signal() = runTest {
        val helper = AppNavigationHelper()
        val signal = async(start = CoroutineStart.UNDISPATCHED) { helper.navigationFlow.first() }

        helper.navigateTo(
            object : Page {
                override fun toRoute(): NavRoute = NavRoute(
                    path = "/tangram/detail",
                    args = mapOf("stageId" to "3")
                )
            }
        )

        assertEquals(
            NavSignal.GoToDestPage(
                NavRoute(path = "/tangram/detail", args = mapOf("stageId" to "3"))
            ),
            signal.await()
        )
    }

    @Test
    fun navigate_by_route_emits_destination_signal() = runTest {
        val helper = AppNavigationHelper()
        val route = NavRoute("/ox/note", mapOf("historyId" to "10"))
        val signal = async(start = CoroutineStart.UNDISPATCHED) { helper.navigationFlow.first() }

        helper.navigateByRoute(route)

        assertEquals(NavSignal.GoToDestPage(route), signal.await())
    }

    @Test
    fun navigate_deep_link_emits_deep_link_signal() = runTest {
        val helper = AppNavigationHelper()
        val route = NavRoute("/map")
        val signal = async(start = CoroutineStart.UNDISPATCHED) { helper.navigationFlow.first() }

        helper.navigateDeepLink(route)

        assertEquals(NavSignal.DeepLink(route), signal.await())
    }

    @Test
    fun navigate_to_back_emits_back_signal() = runTest {
        val helper = AppNavigationHelper()
        val signal = async(start = CoroutineStart.UNDISPATCHED) { helper.navigationFlow.first() }

        helper.navigateToBack()

        assertEquals(NavSignal.Back, signal.await())
    }
}
