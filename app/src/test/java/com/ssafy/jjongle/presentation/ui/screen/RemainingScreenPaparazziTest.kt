package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.ScreenOrientation
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.oxgame.entity.OX
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.tangram.entity.TangramHistory
import com.ssafy.jjongle.presentation.model.CharacterType
import com.ssafy.jjongle.presentation.state.AnimalBookState
import com.ssafy.jjongle.common.presentation.ui.layout.DesignCanvas
import com.ssafy.jjongle.presentation.ui.theme.JjongleTheme
import com.ssafy.jjongle.presentation.viewmodel.QuizNoteState
import com.ssafy.jjongle.presentation.viewmodel.QuizNoteUi
import com.ssafy.jjongle.tangram.presentation.R as TangramR
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import java.time.LocalDateTime
import org.junit.Rule
import org.junit.Test

/**
 * Remaining Screen Tablet Paparazzi Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class RemainingScreenTabletPaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.NEXUS_10
    )

    @Test
    fun setting_tablet_reference_ratio() {
        paparazzi.snapshot { SettingSnapshotContent() }
    }

    @Test
    fun quiz_note_empty_tablet_reference_ratio() {
        paparazzi.snapshot { QuizNoteEmptySnapshotContent() }
    }

    @Test
    fun quiz_note_list_tablet_reference_ratio() {
        paparazzi.snapshot { QuizNoteListSnapshotContent() }
    }

    @Test
    fun animal_book_tablet_reference_ratio() {
        paparazzi.snapshot { AnimalBookSnapshotContent() }
    }

    @Test
    fun camera_chrome_tablet_reference_ratio() {
        paparazzi.snapshot { CameraChromeSnapshotContent() }
    }
}

/**
 * Remaining Screen Phone Paparazzi Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class RemainingScreenPhonePaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(
            orientation = ScreenOrientation.LANDSCAPE
        )
    )

    @Test
    fun setting_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { SettingSnapshotContent() }
    }

    @Test
    fun quiz_note_empty_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { QuizNoteEmptySnapshotContent() }
    }

    @Test
    fun quiz_note_list_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { QuizNoteListSnapshotContent() }
    }

    @Test
    fun animal_book_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { AnimalBookSnapshotContent() }
    }

    @Test
    fun camera_chrome_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { CameraChromeSnapshotContent() }
    }
}

/**
 * Remaining Screen Wide Phone Paparazzi Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class RemainingScreenWidePhonePaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6_PRO.copy(
            orientation = ScreenOrientation.LANDSCAPE
        )
    )

    @Test
    fun setting_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { SettingSnapshotContent() }
    }

    @Test
    fun quiz_note_empty_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { QuizNoteEmptySnapshotContent() }
    }

    @Test
    fun quiz_note_list_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { QuizNoteListSnapshotContent() }
    }

    @Test
    fun animal_book_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { AnimalBookSnapshotContent() }
    }

    @Test
    fun camera_chrome_wide_phone_keeps_tablet_ratio() {
        paparazzi.snapshot { CameraChromeSnapshotContent() }
    }
}

@Composable
private fun RemainingRatioSnapshot(content: @Composable () -> Unit) {
    JjongleTheme {
        DesignCanvas(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun SettingSnapshotContent() {
    RemainingRatioSnapshot {
        SettingContent(
            serverNickname = "탐험가",
            editingNickname = "",
            editingCharacter = CharacterType.MONGI,
            showWithdrawDialog = false,
            isWithdrawing = false,
            onNicknameChange = {},
            onCharacterSelect = {},
            onConfirmClick = {},
            onBackClick = {},
            onWithdrawClick = {},
            onWithdrawDismiss = {},
            onWithdrawConfirm = {}
        )
    }
}

@Composable
private fun QuizNoteEmptySnapshotContent() {
    RemainingRatioSnapshot {
        QuizNoteContent(
            ui = QuizNoteState(notes = persistentListOf(), detail = persistentListOf()),
            onBackClick = {},
            onGoOXGameTitle = {},
            onNoteClick = {},
            onLoadPage = {},
            onCloseDetail = {}
        )
    }
}

@Composable
private fun QuizNoteListSnapshotContent() {
    RemainingRatioSnapshot {
        QuizNoteContent(
            ui = QuizNoteState(
                notes = persistentListOf(
                    QuizNoteUi(1L, LocalDateTime.of(2026, 6, 21, 10, 30)),
                    QuizNoteUi(2L, LocalDateTime.of(2026, 6, 21, 11, 10)),
                    QuizNoteUi(3L, LocalDateTime.of(2026, 6, 21, 11, 50))
                ),
                hasNext = true,
                detail = persistentListOf()
            ),
            onBackClick = {},
            onGoOXGameTitle = {},
            onNoteClick = {},
            onLoadPage = {},
            onCloseDetail = {}
        )
    }
}

@Composable
private fun AnimalBookSnapshotContent() {
    RemainingRatioSnapshot {
        AnimalBookContent(
            ui = AnimalBookState(
                isLoading = false,
                unlockMap = persistentMapOf(
                    AnimalType.TURTLE to TangramHistory(
                        stage = 1,
                        tangramId = 1L,
                        animal = AnimalType.TURTLE
                    ),
                    AnimalType.RABBIT to TangramHistory(
                        stage = 2,
                        tangramId = 2L,
                        animal = AnimalType.RABBIT
                    )
                )
            ),
            currentPage = 0,
            onPageChange = {},
            onBackClick = {},
            onAnimalSpecClick = {},
            onSelectAnimal = {},
            onCloseDetail = {}
        )
    }
}

@Composable
private fun CameraChromeSnapshotContent() {
    RemainingRatioSnapshot {
        CameraChromeContent(
            onBack = {},
            onCapturePhoto = {}
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1F2328)),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painter = painterResource(id = TangramR.drawable.tangram_logo),
                    contentDescription = "Tangram Logo"
                )
            }
        }
    }
}
