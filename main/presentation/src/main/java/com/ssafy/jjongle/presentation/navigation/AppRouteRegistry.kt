package com.ssafy.jjongle.presentation.navigation

import androidx.compose.ui.res.painterResource
import com.ssafy.jjongle.common.entity.BgmGroup
import com.ssafy.jjongle.main.domain.deeplink.RoutePattern
import com.ssafy.jjongle.main.domain.navigation.AnimalBookPage
import com.ssafy.jjongle.main.domain.navigation.CameraPage
import com.ssafy.jjongle.main.domain.navigation.LoginPage
import com.ssafy.jjongle.main.domain.navigation.MapPage
import com.ssafy.jjongle.main.domain.navigation.MyPagePage
import com.ssafy.jjongle.main.domain.navigation.QuizNotePage
import com.ssafy.jjongle.main.domain.navigation.SettingPage
import com.ssafy.jjongle.main.domain.navigation.SignupPage
import com.ssafy.jjongle.main.domain.navigation.SplashPage
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.oxgame.domain.navigation.OXGameIntroPage
import com.ssafy.jjongle.oxgame.domain.navigation.OXGamePage
import com.ssafy.jjongle.oxgame.domain.navigation.OXGameTitlePage
import com.ssafy.jjongle.oxgame.domain.navigation.OXGameTutorialPage
import com.ssafy.jjongle.oxgame.presentation.ui.screen.OXGameScreen
import com.ssafy.jjongle.oxgame.presentation.ui.screen.OXGameTitleScreen
import com.ssafy.jjongle.oxgame.presentation.ui.screen.OXTutorialScreen
import com.ssafy.jjongle.presentation.ui.screen.AnimalBookScreen
import com.ssafy.jjongle.presentation.ui.screen.CameraScreen
import com.ssafy.jjongle.presentation.ui.screen.IntroPages
import com.ssafy.jjongle.presentation.ui.screen.IntroScreen
import com.ssafy.jjongle.presentation.ui.screen.LoginScreen
import com.ssafy.jjongle.presentation.ui.screen.MapScreen
import com.ssafy.jjongle.presentation.ui.screen.MypageScreen
import com.ssafy.jjongle.presentation.ui.screen.QuizNoteScreen
import com.ssafy.jjongle.presentation.ui.screen.SettingScreen
import com.ssafy.jjongle.presentation.ui.screen.SignupScreen
import com.ssafy.jjongle.presentation.ui.screen.SplashScreen
import com.ssafy.jjongle.tangram.domain.navigation.BeforeTangramTutorialPage
import com.ssafy.jjongle.tangram.domain.navigation.TangramIntroPage
import com.ssafy.jjongle.tangram.domain.navigation.TangramStagePage
import com.ssafy.jjongle.tangram.domain.navigation.TangramTitlePage
import com.ssafy.jjongle.tangram.domain.navigation.TangramTutorialPage
import com.ssafy.jjongle.tangram.presentation.ui.screen.BeforeTangramTutorialScreen
import com.ssafy.jjongle.tangram.presentation.ui.screen.TangramStageScreen
import com.ssafy.jjongle.tangram.presentation.ui.screen.TangramTitleScreen
import com.ssafy.jjongle.tangram.presentation.ui.screen.TangramTutorialScreen
import com.ssafy.jjongle.tangram.presentation.R as TangramR

/**
 * App Route Paths는 메인 흐름에서 허용되는 상태나 이벤트 종류를 제한합니다.
 *
 * 문자열이나 숫자 상수 대신 타입 분기로 처리해 잘못된 값이 전달되는 일을 줄입니다.
 */
object AppRoutePaths {
    const val SPLASH = SplashPage.PATH
    const val SIGNUP = SignupPage.PATH
    const val LOGIN = LoginPage.PATH
    const val MAP = MapPage.PATH
    const val MY_PAGE = MyPagePage.PATH
    const val QUIZ_NOTE = QuizNotePage.PATH
    const val ANIMAL_BOOK = AnimalBookPage.PATH
    const val SETTING = SettingPage.PATH
    const val OX_GAME = OXGamePage.PATH
    const val OX_GAME_TITLE = OXGameTitlePage.PATH
    const val OX_GAME_INTRO = OXGameIntroPage.PATH
    const val OX_GAME_TUTORIAL = OXGameTutorialPage.PATH
    const val TANGRAM_TITLE = TangramTitlePage.PATH
    const val TANGRAM_INTRO = TangramIntroPage.PATH
    const val TANGRAM_STAGE = TangramStagePage.PATH
    const val BEFORE_TANGRAM_TUTORIAL = BeforeTangramTutorialPage.PATH
    const val TANGRAM_TUTORIAL = TangramTutorialPage.PATH
    const val CAMERA = CameraPage.PATH

    fun camera(animal: String): String = CameraPage.routePath(animal)
}

val appRoutes = listOf(
    AppRoute(
        path = AppRoutePaths.SPLASH,
        bgmGroup = BgmGroup.WORLD,
        render = { _, navigator ->
            SplashScreen(
                onNavigateToLogin = { navigator.replaceAll(AppRoutePaths.LOGIN) },
                onNavigateToMap = { navigator.replaceAll(AppRoutePaths.MAP) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.SIGNUP,
        bgmGroup = BgmGroup.WORLD,
        render = { args, navigator ->
            SignupScreen(
                idToken = args["idToken"].orEmpty(),
                onNavigateToMap = { navigator.replaceAll(AppRoutePaths.MAP) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.LOGIN,
        bgmGroup = BgmGroup.WORLD,
        render = { _, navigator ->
            LoginScreen(
                onNavigateToMap = { navigator.replaceAll(AppRoutePaths.MAP) },
                onNavigateToSignUp = { idToken ->
                    navigator.replaceAll("${AppRoutePaths.SIGNUP}?idToken=$idToken")
                },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.MAP,
        bgmGroup = BgmGroup.WORLD,
        render = { _, navigator ->
            MapScreen(
                onNavigateToLogin = { navigator.replaceAll(AppRoutePaths.LOGIN) },
                onNavigateToOXGame = { navigator.push(AppRoutePaths.OX_GAME_TITLE) },
                onNavigateToTangram = { navigator.push(AppRoutePaths.TANGRAM_TITLE) },
                onNavigateToMyPage = { navigator.push(AppRoutePaths.MY_PAGE) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.MY_PAGE,
        bgmGroup = BgmGroup.MYPAGE,
        render = { _, navigator ->
            MypageScreen(
                onGoMapClick = { navigator.replaceAll(AppRoutePaths.MAP) },
                onLogoutClick = { navigator.replaceAll(AppRoutePaths.LOGIN) },
                onAnimalBookClick = { navigator.push(AppRoutePaths.ANIMAL_BOOK) },
                onQuizNoteClick = { navigator.push(AppRoutePaths.QUIZ_NOTE) },
                onSettingClick = { navigator.push(AppRoutePaths.SETTING) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.QUIZ_NOTE,
        bgmGroup = BgmGroup.MYPAGE,
        syntheticStack = { args ->
            listOf(GenericNavKey(AppRoutePaths.MY_PAGE), GenericNavKey(AppRoutePaths.QUIZ_NOTE, args))
        },
        render = { _, navigator ->
            QuizNoteScreen(
                onBackClick = { navigator.pop() },
                onGoOXGameTitle = { navigator.push(AppRoutePaths.OX_GAME_TITLE) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.ANIMAL_BOOK,
        bgmGroup = BgmGroup.MYPAGE,
        syntheticStack = { args ->
            listOf(GenericNavKey(AppRoutePaths.MY_PAGE), GenericNavKey(AppRoutePaths.ANIMAL_BOOK, args))
        },
        render = { _, navigator ->
            AnimalBookScreen(
                onBackClick = { navigator.pop() },
                onAnimalSpecClick = { animal -> navigator.push(AppRoutePaths.camera(animal)) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.SETTING,
        bgmGroup = BgmGroup.MYPAGE,
        syntheticStack = { args ->
            listOf(GenericNavKey(AppRoutePaths.MY_PAGE), GenericNavKey(AppRoutePaths.SETTING, args))
        },
        render = { _, navigator ->
            SettingScreen(
                onBackClick = { navigator.pop() },
                onUpdated = { navigator.popTo(AppRoutePaths.MY_PAGE, inclusive = false) },
                onWithdrawn = { navigator.replaceAll(AppRoutePaths.LOGIN) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.OX_GAME,
        bgmGroup = BgmGroup.OX,
        render = { _, navigator ->
            OXGameScreen(
                onNavigateToMap = { navigator.replaceAll(AppRoutePaths.MAP) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.OX_GAME_TITLE,
        bgmGroup = BgmGroup.OX,
        render = { _, navigator ->
            OXGameTitleScreen(
                onStartGameClick = { navigator.push(AppRoutePaths.OX_GAME) },
                onGoMapClick = { navigator.replaceAll(AppRoutePaths.MAP) },
                onGameRulesClick = { navigator.push(AppRoutePaths.OX_GAME_INTRO) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.OX_GAME_INTRO,
        bgmGroup = BgmGroup.OX,
        render = { _, navigator ->
            IntroScreen(
                gameName = "쫑글 O/X 대모험",
                pages = IntroPages.oxGamePages,
                onStartGameClick = {
                    navigator.navigateWithinStack(
                        path = AppRoutePaths.OX_GAME_TITLE,
                        inclusive = false,
                        nextRoute = AppRoutePaths.OX_GAME,
                    )
                },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.OX_GAME_TUTORIAL,
        bgmGroup = BgmGroup.OX,
        render = { _, navigator ->
            OXTutorialScreen(
                onStartQuiz = {
                    navigator.navigateWithinStack(
                        path = AppRoutePaths.OX_GAME_TITLE,
                        inclusive = false,
                        nextRoute = AppRoutePaths.OX_GAME,
                    )
                },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.TANGRAM_TITLE,
        bgmGroup = BgmGroup.TANGRAM,
        render = { _, navigator ->
            TangramTitleScreen(
                gameName = "쫑글 탐험대",
                backgroundImageRes = TangramR.drawable.tangram_title_background,
                onStartGameClick = { navigator.push(AppRoutePaths.TANGRAM_STAGE) },
                onGoMapClick = { navigator.replaceAll(AppRoutePaths.MAP) },
                onGameRulesClick = { navigator.push(AppRoutePaths.TANGRAM_INTRO) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.TANGRAM_INTRO,
        bgmGroup = BgmGroup.TANGRAM,
        render = { _, navigator ->
            IntroScreen(
                gameName = "쫑글 탐험대",
                pages = IntroPages.tangramGamePages,
                onStartGameClick = { navigator.push(AppRoutePaths.TANGRAM_STAGE) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.TANGRAM_STAGE,
        bgmGroup = BgmGroup.TANGRAM,
        syntheticStack = { args ->
            listOf(GenericNavKey(AppRoutePaths.TANGRAM_TITLE), GenericNavKey(AppRoutePaths.TANGRAM_STAGE, args))
        },
        render = { _, navigator ->
            TangramStageScreen(
                gameName = "쫑글 탐험대",
                backgroundImagePainter = painterResource(id = TangramR.drawable.tangram_stage_background),
                onStartGameClick = { navigator.push(AppRoutePaths.BEFORE_TANGRAM_TUTORIAL) },
                onGoMapClick = {
                    navigator.popTo(AppRoutePaths.TANGRAM_TITLE, inclusive = true)
                    navigator.push(AppRoutePaths.TANGRAM_TITLE)
                },
                onMeetAnimalClick = { navigator.push(AppRoutePaths.ANIMAL_BOOK) },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.BEFORE_TANGRAM_TUTORIAL,
        bgmGroup = BgmGroup.TANGRAM,
        syntheticStack = { args ->
            listOf(
                GenericNavKey(AppRoutePaths.TANGRAM_TITLE),
                GenericNavKey(AppRoutePaths.TANGRAM_STAGE),
                GenericNavKey(AppRoutePaths.BEFORE_TANGRAM_TUTORIAL, args),
            )
        },
        render = { _, navigator ->
            BeforeTangramTutorialScreen(
                onStartTutorial = { navigator.push(AppRoutePaths.TANGRAM_TUTORIAL) },
                onSkipTutorial = {
                    navigator.popTo(AppRoutePaths.TANGRAM_STAGE, inclusive = true)
                    navigator.push(AppRoutePaths.TANGRAM_STAGE)
                },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.TANGRAM_TUTORIAL,
        bgmGroup = BgmGroup.TANGRAM,
        syntheticStack = { args ->
            listOf(
                GenericNavKey(AppRoutePaths.TANGRAM_TITLE),
                GenericNavKey(AppRoutePaths.TANGRAM_STAGE),
                GenericNavKey(AppRoutePaths.TANGRAM_TUTORIAL, args),
            )
        },
        render = { _, navigator ->
            TangramTutorialScreen(
                onStartTutorial = {
                    navigator.popTo(AppRoutePaths.TANGRAM_STAGE, inclusive = true)
                    navigator.push(AppRoutePaths.TANGRAM_STAGE)
                },
            )
        },
    ),
    AppRoute(
        path = AppRoutePaths.CAMERA,
        syntheticStack = { args ->
            listOf(GenericNavKey(AppRoutePaths.ANIMAL_BOOK), GenericNavKey(AppRoutePaths.CAMERA, args))
        },
        render = { args, navigator ->
            CameraScreen(
                animal = args["animal"] ?: "turtle",
                onBack = { navigator.pop() },
            )
        },
    ),
)

val appRouteByPath = appRoutes.associateBy { it.path }

val appRoutePatterns = appRoutes
    .map { it.path }
    .filter { it.contains("{") && it.contains("}") }
    .map(::RoutePattern)
