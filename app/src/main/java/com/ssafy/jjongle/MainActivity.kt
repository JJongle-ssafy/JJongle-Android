package com.ssafy.jjongle

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.presentation.jank.JankReporter
import com.ssafy.jjongle.common.presentation.jank.LocalJankReporter
import com.ssafy.jjongle.common.presentation.message.MessageEffectBus
import com.ssafy.jjongle.common.presentation.message.MessageEffectHost
import com.ssafy.jjongle.common.presentation.ui.layout.DesignCanvas
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.presentation.deeplink.resolveRoute
import com.ssafy.jjongle.presentation.navigation.appRouteByPath
import com.ssafy.jjongle.presentation.navigation.appRoutePatterns
import com.ssafy.jjongle.presentation.navigation.AppRoutePaths
import com.ssafy.jjongle.presentation.navigation.GenericNavKey
import com.ssafy.jjongle.presentation.navigation.NavGraph
import com.ssafy.jjongle.presentation.navigation.toComposeRoute
import com.ssafy.jjongle.presentation.navigation.toSyntheticNavStack
import com.ssafy.jjongle.presentation.ui.theme.JjongleTheme
import com.ssafy.jjongle.presentation.viewmodel.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Android 생명주기와 Compose 앱 셸을 연결하는 단일 Activity입니다.
 *
 * 앱 루트 테마, 전역 메시지 호스트, Navigation host를 구성해 기능 모듈 화면을 표시합니다.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationHelper: NavigationHelper

    @Inject
    lateinit var messageEffectBus: MessageEffectBus

    @Inject
    lateinit var jankReporter: JankReporter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val coldStartStack = intent.resolveDeepLinkStartStack()
            .orEmpty()
            .takeUnless { it.isEmpty() }
            ?: listOf(GenericNavKey(AppRoutePaths.SPLASH))

        setContent {
            JjongleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ArchiThemeImpl.archiColor.bgDefaultLevel0,
                ) {
                    val navigationViewModel: NavigationViewModel = viewModel()

                    val bgState = remember { mutableStateOf<Int?>(null) }

                    CompositionLocalProvider(
                        com.ssafy.jjongle.common.presentation.ui.layout.LocalLetterboxImageResController provides bgState,
                        LocalJankReporter provides jankReporter,
                    ) {
                        MessageEffectHost(
                            messageEffectBus = messageEffectBus,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            DesignCanvas(modifier = Modifier.fillMaxSize()) {
                                NavGraph(
                                    startDestination = coldStartStack.first().toComposeRoute(),
                                    initialSyntheticStack = coldStartStack,
                                    navigationViewModel = navigationViewModel,
                                    navigationHelper = navigationHelper,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleWarmDeepLink(intent)
    }

    private fun handleWarmDeepLink(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return
        val route = intent.data?.resolveRoute(
            registeredPaths = appRouteByPath.keys,
            routePatterns = appRoutePatterns,
        ) ?: return

        navigationHelper.navigateDeepLink(route)
    }

    private fun Intent?.resolveDeepLinkStartStack(): List<GenericNavKey>? {
        if (this?.action != Intent.ACTION_VIEW) return null
        return data?.resolveRoute(
            registeredPaths = appRouteByPath.keys,
            routePatterns = appRoutePatterns,
        )?.toSyntheticNavStack()
    }
}
