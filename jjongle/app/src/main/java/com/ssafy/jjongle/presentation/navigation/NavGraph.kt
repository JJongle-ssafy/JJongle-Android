package com.ssafy.jjongle.presentation.navigation

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.screen.AnimalBookScreen
import com.ssafy.jjongle.presentation.ui.screen.CameraScreen
import com.ssafy.jjongle.presentation.ui.screen.IntroPages
import com.ssafy.jjongle.presentation.ui.screen.IntroScreen
import com.ssafy.jjongle.presentation.ui.screen.LoginScreen
import com.ssafy.jjongle.presentation.ui.screen.MapScreen
import com.ssafy.jjongle.presentation.ui.screen.MypageScreen
import com.ssafy.jjongle.presentation.ui.screen.OXGameScreen
import com.ssafy.jjongle.presentation.ui.screen.OXGameTitleScreen
import com.ssafy.jjongle.presentation.ui.screen.OXTutorialScreen
import com.ssafy.jjongle.presentation.ui.screen.QuizNoteScreen
import com.ssafy.jjongle.presentation.ui.screen.SettingScreen
import com.ssafy.jjongle.presentation.ui.screen.SignupScreen
import com.ssafy.jjongle.presentation.ui.screen.SplashScreen
import com.ssafy.jjongle.presentation.ui.screen.TangramStageScreen
import com.ssafy.jjongle.presentation.ui.screen.TangramTitleScreen
import com.ssafy.jjongle.presentation.ui.screen.BeforeTangramTutorialScreen
import com.ssafy.jjongle.presentation.ui.screen.TangramTutorialScreen
import com.ssafy.jjongle.presentation.viewmodel.MapViewModel
import com.ssafy.jjongle.presentation.viewmodel.MusicViewModel
import com.ssafy.jjongle.presentation.viewmodel.NavigationViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route,
    navigationViewModel: NavigationViewModel
) {
    // 현재 라우트를 추적
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val mapViewModel: MapViewModel = hiltViewModel()
    val musicViewModel: MusicViewModel = hiltViewModel()  // BGM 전환용 뷰모델 주입
    
    // 튜토리얼용 토큰 저장
    var tutorialAccessToken by remember { mutableStateOf<String?>(null) }
    var tutorialRefreshToken by remember { mutableStateOf<String?>(null) }

    // 의도적인 네비게이션만 저장 (초기 로딩 시에는 저장하지 않음)
    LaunchedEffect(currentRoute) {
        currentRoute?.let { route ->
            // 스플래시에서 시작하는 경우가 아니라면 저장
            if (route != Screen.Splash.route) {
                navigationViewModel.saveRoute(route)
            }
        }
    }

    // 라우트 변경 시 BGM 그룹 평가 + 전환
    LaunchedEffect(currentRoute) {
        musicViewModel.onRouteChanged(currentRoute)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 스플래시 화면
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        // 로그인 화면
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { idToken ->
                    navController.navigate("${Screen.Signup.route}?idToken=$idToken") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }


        // 회원가입 화면
        composable(
            route = "${Screen.Signup.route}?idToken={idToken}",
            arguments = listOf(
                navArgument("idToken") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val idToken = backStackEntry.arguments?.getString("idToken") ?: ""

            SignupScreen(
                idToken = idToken,
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }

        // 맵 화면
        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToOXGame = {
                    navController.navigate(Screen.OXGameTitle.route)
                },
                onNavigateToTangram = {
                    navController.navigate(Screen.TangramTitle.route)
                },
                onNavigateToMyPage = {
                    navController.navigate(Screen.MyPage.route)
                },
                viewModel = mapViewModel,
            )
        }

        // OX 게임 타이틀 화면
        composable(Screen.OXGameTitle.route) {
            OXGameTitleScreen(
                gameName = "쫑글 O/X 대모험",
                backgroundImagePainter = painterResource(id = R.drawable.ox_title_background),
                onStartGameClick = {
                    // 친구들과 문제를 풀어볼까요? → 튜토리얼
//                    navController.navigate(Screen.OXGameTutorial.route)
                    // 바로 게임 시작으로 수정
                    navController.navigate(Screen.OXGame.route)
                },
                onGoMapClick = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Map.route) { inclusive = true }
                    }
                },
                onGameRulesClick = {
                    navController.navigate(Screen.OXGameIntro.route)
                }
            )
        }

        // OX 게임 인트로 화면 (놀이 설명 → 모험 떠나기 → 바로 게임 시작)
        composable(Screen.OXGameIntro.route) {
            IntroScreen(
                gameName = "쫑글 O/X 대모험",
                pages = IntroPages.oxGamePages,
                onStartGameClick = {
                    navController.navigate(Screen.OXGame.route) {
                        popUpTo(Screen.OXGameTitle.route) { inclusive = false }
                    }
                }
            )
        }

        // OX 튜토리얼 화면
        composable(Screen.OXGameTutorial.route) {
            OXTutorialScreen(
                onStartQuiz = {
                    navController.navigate(Screen.OXGame.route) {
                        popUpTo(Screen.OXGameTitle.route) { inclusive = false }
                    }
                }
            )
        }

        // OX 게임 화면
        composable(Screen.OXGame.route) {
            OXGameScreen(
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Map.route) { inclusive = true }
                    }
                },
            )
        }

        // 칠교놀이 타이틀 화면
        composable(Screen.TangramTitle.route) {
            val context = LocalContext.current
            TangramTitleScreen(
                gameName = "쫑글 탐험대",
                backgroundImagePainter = painterResource(id = R.drawable.tangram_title_background),
                onStartGameClick = {
//                    val intent = Intent(context, UnityGameActivity::class.java)
//                    context.startActivity(intent)
                    navController.navigate(Screen.TangramStage.route)
                },
                onGoMapClick = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Map.route) { inclusive = true }
                    }
                },
                onGameRulesClick = {
                    navController.navigate(Screen.TangramIntro.route)
                }
            )
        }

        // 칠교놀이 스테이지 화면
        composable(Screen.TangramStage.route) {
            val context = LocalContext.current

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { res ->
                if (res.resultCode == Activity.RESULT_OK) {
                    val data = res.data?.getStringExtra("unityResult") ?: ""
                    // TODO: 결과 처리 (예: accessToke, refreshToken 재설정)
                    // navController.navigate("tangramResult?payload=${URLEncoder.encode(data, "utf-8")}")
                }
            }

            TangramStageScreen(
                gameName = "쫑글 탐험대",
                backgroundImagePainter = painterResource(id = R.drawable.tangram_stage_background),
                onStartGameClick = { stageId, accessToken, refreshToken ->
                    if (stageId == 1) {
                        // 스테이지 1은 튜토리얼 플로우로 이동 (토큰은 NavGraph 범위로 저장)
                        tutorialAccessToken = accessToken
                        tutorialRefreshToken = refreshToken
                        navController.navigate(Screen.BeforeTangramTutorial.route)
                    } else {
                        // 다른 스테이지는 바로 유니티 시작
                        val intent = Intent(
                            context,
                            Class.forName("com.ssafy.jjongle.CustomUnityActivity")
                        ).apply {
                            putExtra("accessToken", accessToken ?: "")
                            putExtra("refreshToken", refreshToken ?: "")
                            putExtra("stageId", stageId)
                        }
                        launcher.launch(intent) // ✅ 결과 받기 모드로 실행
                        // 나중에 결과 받기 모드 아닌걸로 변경
                    }
                },
                onGoMapClick = {
                    navController.navigate(Screen.TangramTitle.route) {
                        popUpTo(Screen.TangramTitle.route) { inclusive = true }
                    }
                },
                onMeetAnimalClick = {
                    navController.navigate(Screen.AnimalBook.route)
                },
            )
        }


        // 칠교놀이 인트로 화면
        composable(Screen.TangramIntro.route) {

            val context = LocalContext.current
            IntroScreen(
                gameName = "쫑글 탐험대",
                pages = IntroPages.tangramGamePages,
                onStartGameClick = {
//                    val intent = Intent(context, UnityGameActivity::class.java)
//                    context.startActivity(intent)
                    navController.navigate(Screen.TangramStage.route)
                }
            )
        }

        // 마이페이지 화면 구현
        composable(Screen.MyPage.route) {
            val context = LocalContext.current
            MypageScreen(
                // 맵으로 돌아가기
                onGoMapClick = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Map.route) { inclusive = true }
                    }
                },

                // 로그아웃
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },


                // 동물도감
                onAnimalBookClick = {
                    // TODO: 네비게이션 목적지를 채워 주세요
                    navController.navigate(Screen.AnimalBook.route) {
                    }
                },

                // 지식노트
                onQuizNoteClick = {
                    navController.navigate(Screen.QuizNote.route)
                },

                // 설정
                onSettingClick = {
                    navController.navigate(Screen.Setting.route) {
                    }
                },
            )
        }

        // 지식노트 (OX 오답노트) 화면
        composable(Screen.QuizNote.route) {
            QuizNoteScreen(

                // 맵으로 돌아가기
                onBackClick = { navController.popBackStack() },
                // OX 게임 화면으로 이동
                onGoOXGameTitle = {
                    navController.navigate(Screen.OXGameTitle.route) {
                    }
                },

                )
        }

        // 동물 친구들 (칠교 동물도감) 화면
        composable(Screen.AnimalBook.route) {
            AnimalBookScreen(

                // 뒤로 가기
                onBackClick = { navController.popBackStack() },

                // 동물 상세 보기
                onAnimalSpecClick = { animal ->
                    navController.navigate(Screen.Camera.createRoute(animal))
                }
            )
        }

        // 회원 정보 수정 화면
        composable(Screen.Setting.route) {
            SettingScreen(

                // 뒤로 가기
                onBackClick = { navController.popBackStack() },

                // 회원 정보 수정 완료
                onUpdated = {
                    navController.popBackStack(Screen.MyPage.route, inclusive = false)
                },

                // 회원 탈퇴 완료
                onWithdrawn = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )

        }

        // 칠교 튜토리얼 시작 전 화면
        composable(Screen.BeforeTangramTutorial.route) {
            val context = LocalContext.current
            
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { res ->
                // 유니티가 끝나면 TangramStageScreen으로 이동
                navController.navigate(Screen.TangramStage.route) {
                    popUpTo(Screen.TangramStage.route) { inclusive = true }
                }
            }
            
            BeforeTangramTutorialScreen(
                onStartTutorial = {
                    navController.navigate(Screen.TangramTutorial.route)
                },
                onSkipTutorial = {
                    val intent = Intent(
                        context,
                        Class.forName("com.ssafy.jjongle.CustomUnityActivity")
                    ).apply {
                        putExtra("accessToken", tutorialAccessToken ?: "")
                        putExtra("refreshToken", tutorialRefreshToken ?: "")
                        putExtra("stageId", 1)
                    }
                    launcher.launch(intent)
                }
            )
        }

        // 칠교 튜토리얼 화면
        composable(Screen.TangramTutorial.route) {
            val context = LocalContext.current
            
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { res ->
                // 유니티가 끝나면 TangramStageScreen으로 이동
                navController.navigate(Screen.TangramStage.route) {
                    popUpTo(Screen.TangramStage.route) { inclusive = true }
                }
            }
            
            TangramTutorialScreen(
                onStartTutorial = {
                    val intent = Intent(
                        context,
                        Class.forName("com.ssafy.jjongle.CustomUnityActivity")
                    ).apply {
                        putExtra("accessToken", tutorialAccessToken ?: "")
                        putExtra("refreshToken", tutorialRefreshToken ?: "")
                        putExtra("stageId", 1)
                    }
                    launcher.launch(intent)
                }
            )
        }

        // 카메라 화면
        composable(
            route = Screen.Camera.route,
            arguments = listOf(navArgument("animal") { type = NavType.StringType })
        ) { backStackEntry ->
            val animal = backStackEntry.arguments?.getString("animal") ?: "turtle"
            CameraScreen(
                animal = animal,
                onBack = { navController.popBackStack() }
            )
        }


    }
}

