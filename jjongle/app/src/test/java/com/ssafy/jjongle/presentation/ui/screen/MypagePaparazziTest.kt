package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.ScreenOrientation
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.layout.DesignCanvas
import com.ssafy.jjongle.presentation.ui.theme.JjongleTheme
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.res.painterResource

class MypageTabletPaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.NEXUS_10
    )

    @Test
    fun mypage_tablet_reference_ratio() {
        paparazzi.snapshot {
            MypageSnapshotContent()
        }
    }

    @Test
    fun ox_title_tablet_reference_ratio() {
        paparazzi.snapshot {
            OXTitleSnapshotContent()
        }
    }

    @Test
    fun tangram_title_tablet_reference_ratio() {
        paparazzi.snapshot {
            TangramTitleSnapshotContent()
        }
    }
}

class MypagePhonePaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(
            orientation = ScreenOrientation.LANDSCAPE
        )
    )

    @Test
    fun mypage_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            MypageSnapshotContent()
        }
    }

    @Test
    fun ox_title_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            OXTitleSnapshotContent()
        }
    }

    @Test
    fun tangram_title_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            TangramTitleSnapshotContent()
        }
    }
}

@Composable
private fun MypageSnapshotContent() {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            MypageContent(
                nickname = "사용자",
                profileImageRes = R.drawable.profile_mongi,
                onAnimalBookClick = {},
                onQuizNoteClick = {},
                onSettingClick = {},
                onGoMapClick = {},
                onLogoutClick = {}
            )
        }
    }
}

@Composable
private fun OXTitleSnapshotContent() {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            OXGameTitleScreen(
                gameName = "쫑글 O/X 대모험",
                backgroundImagePainter = painterResource(id = R.drawable.ox_title_background),
                onStartGameClick = {},
                onGoMapClick = {},
                onGameRulesClick = {}
            )
        }
    }
}

@Composable
private fun TangramTitleSnapshotContent() {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            TangramTitleScreen(
                gameName = "쫑글 탐험대",
                backgroundImagePainter = painterResource(id = R.drawable.tangram_title_background),
                onStartGameClick = {},
                onGoMapClick = {},
                onGameRulesClick = {}
            )
        }
    }
}
