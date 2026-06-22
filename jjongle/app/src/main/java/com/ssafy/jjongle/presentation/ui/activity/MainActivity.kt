package com.ssafy.jjongle.presentation.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ssafy.jjongle.presentation.navigation.NavGraph
import com.ssafy.jjongle.presentation.navigation.Screen
import com.ssafy.jjongle.presentation.ui.theme.JjongleTheme
import com.ssafy.jjongle.presentation.viewmodel.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.jjongle.data.local.AuthDataSource
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authDataSource: AuthDataSource

    // 👇 onNewIntent에서도 navigation을 쓰기 위해 Activity 프로퍼티로 꺼냄
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JjongleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationViewModel: NavigationViewModel = viewModel()
                    val currentRoute by navigationViewModel.currentRoute.collectAsState()

                    // 👇 navController를 Activity 프로퍼티에 보관
                    navController = rememberNavController()

                    Log.d("MainActivity", "Current Route: $currentRoute")

                    NavGraph(
                        navController = navController,
                        startDestination = Screen.Splash.route,
//                        startDestination = Screen.Camera.route,
                        navigationViewModel = navigationViewModel
                    )

                    // ✅ 액티비티 최초 진입 인텐트 처리
                    //    (setContent 이후에 한 번만 처리되면 충분)
                    handleUnityIntentIfAny(intent)
                }
            }
        }
    }

    // ✅ singleTop + clearTop으로 재진입될 때도 처리
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (::navController.isInitialized) {
            handleUnityIntentIfAny(intent)
        } else {
            Log.w("MainActivity", "navController not initialized yet; intent will be handled onCreate")
        }
    }

    /**
     * Unity에서 보낸 인텐트가 있으면 파싱해서 해당 화면으로 이동
     * extras:
     *  - "dest_from_unity": "stage" | "camera" | …(필요시 확장)
     *  - "payload_from_unity": JSON 등 문자열 (Uri.encode 처리해서 쿼리에 실어 보냄)
     */
    private fun handleUnityIntentIfAny(intent: Intent?) {
        intent ?: return
        val dest = intent.getStringExtra("dest_from_unity") ?: return
        val payload = intent.getStringExtra("payload_from_unity") ?: ""

        Log.d("MainActivity", "handleUnityIntentIfAny: dest=$dest, payload=$payload")

        // 모든 경우에 토큰 업데이트 시도
        handleTokenUpdate(payload)

        when (dest) {
            "stage" -> {
                // 예: 스테이지 화면으로 이동 (쿼리 파라미터로 데이터 전달)
                val encoded = Uri.encode(payload)
//                navController.navigate("${Screen.TangramStage.route}?payload=$encoded") {
//                    // 필요 시 백스택 정책 조절
//                    // popUpTo(navController.graph.startDestinationId) { inclusive = false }
//                    launchSingleTop = true
//                }
            }
            "camera" -> {
                try {
                    val jsonObject = JSONObject(payload)
                    val animal = jsonObject.getString("animal").lowercase()
                    navController.navigate(Screen.Camera.createRoute(animal)) {
//                        launchSingleTop = true
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "카메라 화면 이동 실패: ${e.message}", e)
                }
            }
            "finish" -> {
                // finish 케이스에서는 추가 처리 없음 (토큰 업데이트는 이미 위에서 처리됨)
            }
            else -> {
                Log.w("MainActivity", "Unknown dest_from_unity: $dest")
            }
        }

        // 한 번 소비했으면 extras 비워주면 깔끔 (선택)
        intent.removeExtra("dest_from_unity")
        intent.removeExtra("payload_from_unity")
    }

    private fun handleTokenUpdate(payload: String) {
        try {
            val jsonObject = JSONObject(payload)
            val accessToken = jsonObject.getString("accessToken")
            val refreshToken = jsonObject.getString("refreshToken")
            
            authDataSource.saveTokens(accessToken, refreshToken)
            Log.d(
                "MainActivity",
                "토큰 업데이트 완료. hasAccessToken=${accessToken.isNotBlank()}, hasRefreshToken=${refreshToken.isNotBlank()}"
            )
        } catch (e: Exception) {
            Log.e("MainActivity", "토큰 업데이트 실패: ${e.message}", e)
        }
    }
}
