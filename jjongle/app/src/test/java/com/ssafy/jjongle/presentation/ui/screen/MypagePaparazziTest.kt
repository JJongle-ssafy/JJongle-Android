package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.ScreenOrientation
import com.ssafy.jjongle.R
import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.presentation.ui.layout.DesignCanvas
import com.ssafy.jjongle.presentation.ui.theme.JjongleTheme
import org.junit.Rule
import org.junit.Test

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

    @Test
    fun before_tangram_tutorial_tablet_reference_ratio() {
        paparazzi.snapshot {
            BeforeTangramTutorialSnapshotContent()
        }
    }

    @Test
    fun ox_game_start_tablet_reference_ratio() {
        paparazzi.snapshot {
            OXGameStartSnapshotContent()
        }
    }

    @Test
    fun ox_game_explanation_tablet_reference_ratio() {
        paparazzi.snapshot {
            OXGameExplanationSnapshotContent()
        }
    }

    @Test
    fun ox_game_result_tablet_reference_ratio() {
        paparazzi.snapshot {
            OXGameResultSnapshotContent()
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

    @Test
    fun before_tangram_tutorial_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            BeforeTangramTutorialSnapshotContent()
        }
    }

    @Test
    fun ox_game_start_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            OXGameStartSnapshotContent()
        }
    }

    @Test
    fun ox_game_explanation_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            OXGameExplanationSnapshotContent()
        }
    }

    @Test
    fun ox_game_result_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            OXGameResultSnapshotContent()
        }
    }
}

class MypageWidePhonePaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6_PRO.copy(
            orientation = ScreenOrientation.LANDSCAPE
        )
    )

    @Test
    fun mypage_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            MypageSnapshotContent()
        }
    }

    @Test
    fun ox_title_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            OXTitleSnapshotContent()
        }
    }

    @Test
    fun tangram_title_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            TangramTitleSnapshotContent()
        }
    }

    @Test
    fun before_tangram_tutorial_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            BeforeTangramTutorialSnapshotContent()
        }
    }

    @Test
    fun ox_game_start_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            OXGameStartSnapshotContent()
        }
    }

    @Test
    fun ox_game_explanation_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            OXGameExplanationSnapshotContent()
        }
    }

    @Test
    fun ox_game_result_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot {
            OXGameResultSnapshotContent()
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

@Composable
private fun BeforeTangramTutorialSnapshotContent() {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            BeforeTangramTutorialScreen(
                onStartTutorial = {},
                onSkipTutorial = {}
            )
        }
    }
}

@Composable
private fun OXGameStartSnapshotContent() {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            GameStartContent(onStartQuiz = {})
        }
    }
}

@Composable
private fun OXGameExplanationSnapshotContent() {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            QuizExplanationContent(
                quiz = sampleQuiz,
                quizIndex = 0,
                totalQuizzes = 3,
                isAnswerSubmitted = true,
                onNextQuiz = {}
            )
        }
    }
}

@Composable
private fun OXGameResultSnapshotContent() {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            GameResultContent(
                top3Rankings = listOf(1 to 3, 2 to 2, 3 to 1),
                profiles = emptyMap(),
                onRestartGame = {},
                onBackToMenu = {}
            )
        }
    }
}

private val sampleQuiz = Quiz(
    id = 1,
    question = "바나나는 노란색일까요?",
    answer = "O",
    description = "맞아요. 익은 바나나는 보통 노란색이에요."
)
