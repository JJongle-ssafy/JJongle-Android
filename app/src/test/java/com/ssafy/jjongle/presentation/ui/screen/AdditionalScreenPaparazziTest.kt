package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.ScreenOrientation
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.presentation.model.CharacterType
import com.ssafy.jjongle.common.presentation.ui.layout.DesignCanvas
import com.ssafy.jjongle.oxgame.presentation.ui.screen.OXTutorialContent
import com.ssafy.jjongle.presentation.ui.theme.JjongleTheme
import com.ssafy.jjongle.tangram.presentation.R as TangramR
import com.ssafy.jjongle.tangram.presentation.ui.screen.TangramStageContent
import com.ssafy.jjongle.tangram.presentation.ui.screen.TangramTutorialContent
import org.junit.Rule
import org.junit.Test

/**
 * Additional Screen Tablet Paparazzi Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class AdditionalScreenTabletPaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.NEXUS_10
    )

    @Test
    fun splash_tablet_reference_ratio() {
        paparazzi.snapshot { SplashSnapshotContent() }
    }

    @Test
    fun login_tablet_reference_ratio() {
        paparazzi.snapshot { LoginSnapshotContent() }
    }

    @Test
    fun signup_tablet_reference_ratio() {
        paparazzi.snapshot { SignupSnapshotContent() }
    }

    @Test
    fun ox_intro_tablet_reference_ratio() {
        paparazzi.snapshot { OXIntroSnapshotContent() }
    }

    @Test
    fun tangram_intro_tablet_reference_ratio() {
        paparazzi.snapshot { TangramIntroSnapshotContent() }
    }

    @Test
    fun ox_tutorial_tablet_reference_ratio() {
        paparazzi.snapshot { OXTutorialSnapshotContent() }
    }

    @Test
    fun tangram_tutorial_tablet_reference_ratio() {
        paparazzi.snapshot { TangramTutorialSnapshotContent() }
    }

    @Test
    fun map_tablet_reference_ratio() {
        paparazzi.snapshot { MapSnapshotContent() }
    }

    @Test
    fun tangram_stage_tablet_reference_ratio() {
        paparazzi.snapshot { TangramStageSnapshotContent() }
    }
}

/**
 * Additional Screen Phone Paparazzi Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class AdditionalScreenPhonePaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(
            orientation = ScreenOrientation.LANDSCAPE
        )
    )

    @Test
    fun splash_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { SplashSnapshotContent() }
    }

    @Test
    fun login_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { LoginSnapshotContent() }
    }

    @Test
    fun signup_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { SignupSnapshotContent() }
    }

    @Test
    fun ox_intro_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { OXIntroSnapshotContent() }
    }

    @Test
    fun tangram_intro_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { TangramIntroSnapshotContent() }
    }

    @Test
    fun ox_tutorial_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { OXTutorialSnapshotContent() }
    }

    @Test
    fun tangram_tutorial_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { TangramTutorialSnapshotContent() }
    }

    @Test
    fun map_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { MapSnapshotContent() }
    }

    @Test
    fun tangram_stage_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { TangramStageSnapshotContent() }
    }
}

/**
 * Additional Screen Wide Phone Paparazzi Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class AdditionalScreenWidePhonePaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6_PRO.copy(
            orientation = ScreenOrientation.LANDSCAPE
        )
    )

    @Test
    fun splash_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { SplashSnapshotContent() }
    }

    @Test
    fun login_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { LoginSnapshotContent() }
    }

    @Test
    fun signup_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { SignupSnapshotContent() }
    }

    @Test
    fun ox_intro_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { OXIntroSnapshotContent() }
    }

    @Test
    fun tangram_intro_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { TangramIntroSnapshotContent() }
    }

    @Test
    fun ox_tutorial_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { OXTutorialSnapshotContent() }
    }

    @Test
    fun tangram_tutorial_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { TangramTutorialSnapshotContent() }
    }

    @Test
    fun map_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { MapSnapshotContent() }
    }

    @Test
    fun tangram_stage_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { TangramStageSnapshotContent() }
    }
}

@Composable
private fun RatioSnapshot(content: @Composable () -> Unit) {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun SplashSnapshotContent() {
    RatioSnapshot {
        SplashContent(
            isAuthenticated = false,
            onNavigateToLogin = {},
            onNavigateToMap = {}
        )
    }
}

@Composable
private fun LoginSnapshotContent() {
    RatioSnapshot {
        LoginContent(
            isLoading = false,
            errorMessage = null,
            onGoogleSignInClick = {}
        )
    }
}

@Composable
private fun SignupSnapshotContent() {
    RatioSnapshot {
        SignupContent(
            nickname = "",
            selectedCharacter = CharacterType.MONGI,
            onNicknameChange = {},
            onCharacterSelect = {},
            onConfirmClick = {}
        )
    }
}

@Composable
private fun OXIntroSnapshotContent() {
    RatioSnapshot {
        IntroContent(
            gameName = "쫑글 O/X 대모험",
            pages = IntroPages.oxGamePages,
            currentPage = 0,
            onPageChange = {},
            onStartGameClick = {}
        )
    }
}

@Composable
private fun TangramIntroSnapshotContent() {
    RatioSnapshot {
        IntroContent(
            gameName = "쫑글 탐험대",
            pages = IntroPages.tangramGamePages,
            currentPage = 0,
            onPageChange = {},
            onStartGameClick = {}
        )
    }
}

@Composable
private fun OXTutorialSnapshotContent() {
    RatioSnapshot {
        OXTutorialContent(
            page = 0,
            onPrevious = {},
            onNext = {},
            onStartQuiz = {}
        )
    }
}

@Composable
private fun TangramTutorialSnapshotContent() {
    RatioSnapshot {
        TangramTutorialContent(
            currentTutorial = 0,
            onPrevious = {},
            onNext = {},
            onStartTutorial = {}
        )
    }
}

@Composable
private fun MapSnapshotContent() {
    RatioSnapshot {
        MapContent(
            characterX = 371.875f,
            characterY = 678.9f,
            characterTargetX = 371.875f,
            isWalking = false,
            isBgmOn = true,
            onTangramPanelClick = {},
            onOXPanelClick = {},
            onMypagePanelClick = {},
            onBgmClick = {}
        )
    }
}

@Composable
private fun TangramStageSnapshotContent() {
    RatioSnapshot {
        TangramStageContent(
            gameName = "쫑글 탐험대",
            backgroundImagePainter = painterResource(id = TangramR.drawable.tangram_stage_background),
            characterX = 140f,
            characterY = 250f,
            isCharacterMoving = false,
            onStageClick = {},
            onGoMapClick = {},
            onMeetAnimalClick = {}
        )
    }
}
