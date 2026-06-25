package com.ssafy.jjongle.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.oxgame.entity.OX
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import com.ssafy.jjongle.common.presentation.ui.component.BaseButton
import com.ssafy.jjongle.common.presentation.ui.component.ResponsiveBackgroundImage
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.presentation.viewmodel.QuizNoteIntent
import com.ssafy.jjongle.presentation.viewmodel.QuizNoteState
import com.ssafy.jjongle.presentation.viewmodel.QuizNoteUi
import com.ssafy.jjongle.presentation.viewmodel.QuizNoteViewModel
import java.time.LocalDateTime

/**
 * QuizNoteScreen Compose UI를 구성합니다.
 *
 * - 계층: main/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
@Composable
fun QuizNoteScreen(
    onBackClick: () -> Unit,
    onGoOXGameTitle: () -> Unit,   // 빈 상태 CTA용
    vm: QuizNoteViewModel = hiltViewModel()
) {
    val ui by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.onIntent(QuizNoteIntent.LoadPage(0)) }

    // 상세일 때만 시스템 뒤로가기로 닫기
    if (ui.detail.isNotEmpty()) BackHandler { vm.onIntent(QuizNoteIntent.CloseDetail) }

    QuizNoteContent(
        ui = ui,
        onBackClick = onBackClick,
        onGoOXGameTitle = onGoOXGameTitle,
        onNoteClick = { vm.onIntent(QuizNoteIntent.OpenDetail(it)) },
        onLoadPage = { vm.onIntent(QuizNoteIntent.LoadPage(it)) },
        onCloseDetail = { vm.onIntent(QuizNoteIntent.CloseDetail) }
    )
}

@Composable
fun QuizNoteContent(
    ui: QuizNoteState,
    onBackClick: () -> Unit,
    onGoOXGameTitle: () -> Unit,
    onNoteClick: (historyId: Long) -> Unit,
    onLoadPage: (page: Int) -> Unit,
    onCloseDetail: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        // 배경
        ResponsiveBackgroundImage(
            painter = painterResource(R.drawable.mypage_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // 상단 뒤로가기 (오버레이보다 위)
        BaseButton(
            onClick = { if (ui.detail.isNotEmpty()) onCloseDetail() else onBackClick() },
            text = "뒤로가기",
            textStyle = ArchiThemeImpl.typeScale.textStrongM,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 24.dp)
                .zIndex(10f)
        )

        val hasNotes = ui.notes.isNotEmpty()

        if (hasNotes) {
            // 노트 3장
            NotesRow(
                notes = ui.notes,
                onClick = onNoteClick,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
            )

            // 좌/우 화살표 (상세 아닐 때만)
            val showArrows = ui.detail.isEmpty()
            if (showArrows && ui.page > 0) {
                Image(
                    painter = painterResource(R.drawable.previous_btn),
                    contentDescription = "이전",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(80.dp)
                        .padding(start = 24.dp)
                        .clickable { onLoadPage(ui.page - 1) }
                )
            }
            if (showArrows && ui.hasNext) {
                Image(
                    painter = painterResource(R.drawable.next_btn),
                    contentDescription = "다음",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(80.dp)
                        .padding(end = 24.dp)
                        .clickable { onLoadPage(ui.page + 1) }
                )
            }
        } else {
            // 빈 상태 UI
            EmptyNoteCta(
                onStart = onGoOXGameTitle,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 상세 오버레이
        if (ui.detail.isNotEmpty()) {
            QuizDetailOverlay(
                notes = ui.detail,
                onDismiss = onCloseDetail
            )
        }
    }
}

/* ---------- 리스트 렌더 ---------- */

@Composable
private fun NotesRow(
    notes: List<QuizNoteUi>,
    onClick: (historyId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val notePainter = painterResource(R.drawable.note)
    val ratio = remember {
        val s = notePainter.intrinsicSize
        if (s.width > 0 && s.height > 0) s.width / s.height else 260f / 340f
    }

    val containerWidth = 1000.dp
    val spacing = 16.dp
    val columns = 3
    val cardWidth = (containerWidth - spacing * (columns - 1)) / columns
    val cardHeight = cardWidth / ratio

    Box(modifier) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .width(containerWidth)
                .height(cardHeight),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            notes.forEach { note ->
                NoteCard(
                    text = formatNoteText(note.recordedAt),
                    onClick = { onClick(note.id) },
                    modifier = Modifier
                        .width(cardWidth)
                        .aspectRatio(ratio)
                )
            }
        }
    }
}

@Composable
private fun NoteCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(R.drawable.note),
            contentDescription = "quiz note",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        ArchiText(
            text = text,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            style = ArchiThemeImpl.typeScale.textStrongL,
            textAlign = TextAlign.Center,
            color = ArchiThemeImpl.archiColor.contentDefaultLevel1,
        )
    }
}

/* ---------- 상세 오버레이 ---------- */

@Composable
private fun QuizDetailOverlay(
    notes: List<OXGameWrongAnswerNote>,
    onDismiss: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            // 뒤 요소 터치 차단 (버튼은 zIndex로 위에 둠)
            .zIndex(5f)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        event.changes.forEach { it.consume() }
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.quiz_spec_note),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // 중앙 80% 컨테이너(높이/폭 조절은 여기서)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)   // 필요시 0.8f 등으로 조절
                        .fillMaxHeight(0.45f)  // 필요시 0.8f 등으로 조절
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.Start
                ) {
                    notes.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ArchiText(
                                text = item.question,
                                modifier = Modifier.weight(1f),
                                style = ArchiThemeImpl.typeScale.titleStrongM,
                                color = ArchiThemeImpl.archiColor.contentDefaultLevel1,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val oRes = if (item.answer == OX.O)
                                    R.drawable.o_colored else R.drawable.o_uncolored
                                val xRes = if (item.answer == OX.X)
                                    R.drawable.x_colored else R.drawable.x_uncolored

                                Image(painterResource(oRes), "O", Modifier.size(80.dp))
                                Image(painterResource(xRes), "X", Modifier.size(80.dp))
                            }
                        }
                        Spacer(Modifier.height(1.dp))
                    }
                }
            }
        }
    }
}

/* ---------- 빈 상태 CTA ---------- */
@Composable
private fun EmptyNoteCta(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val p = painterResource(R.drawable.none_note)
    val ratio = remember {
        val s = p.intrinsicSize
        if (s.width > 0 && s.height > 0) s.width / s.height else 980f / 620f
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Image(
            painter = p,
            contentDescription = "empty note",
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .aspectRatio(ratio),
            contentScale = ContentScale.Fit
        )
        BaseButton(
            onClick = onStart,
            text = "모험 떠나기",
            textStyle = ArchiThemeImpl.typeScale.textStrongL
        )
    }
}

/* ---------- 유틸 ---------- */
private fun formatNoteText(dt: LocalDateTime): String {
    val d = "${dt.monthValue}월 ${dt.dayOfMonth}일"
    val t = "${dt.hour}시 ${dt.minute}분"
    return "$d\n$t\n지식 조각"
}
