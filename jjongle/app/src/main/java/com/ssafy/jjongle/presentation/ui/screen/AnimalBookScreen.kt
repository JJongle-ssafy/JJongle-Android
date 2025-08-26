package com.ssafy.jjongle.presentation.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.presentation.state.AnimalSlot
import com.ssafy.jjongle.presentation.ui.component.BaseButton
import com.ssafy.jjongle.presentation.ui.mapper.toImageRes
import com.ssafy.jjongle.presentation.viewmodel.AnimalBookViewModel


// 앨범 페이지 모델 (8칸/페이지)
data class AnimalPage(val slots: List<AnimalSlot>)

// ──────────────────────────────────────────────────────────────
// AnimalBookScreen
//  - 정해진 스테이지 순서(9종)대로 기본 잠금 출력
//  - 히스토리 존재 시 해당 슬롯만 unlock + 동물 이미지 노출
//  - 앨범 이미지 위(동일 크기)에 그리드 오버레이
// ──────────────────────────────────────────────────────────────
@Composable
fun AnimalBookScreen(
    onBackClick: () -> Unit,
    onAnimalSpecClick: (String) -> Unit, // (미사용) 필요 시 상세 라우팅에 활용
    viewModel: AnimalBookViewModel = hiltViewModel()
) {
    // 1) 상태 구독 & 뒤로가기(상세 → 그리드 복귀)
    val ui by viewModel.ui.collectAsState()
    BackHandler(enabled = ui.selected != null) { viewModel.closeDetail() }

    // 2) 페이지(8칸 고정) 구성 — 더미 레이아웃: 위치/순서만 담당
    var currentPage by remember { mutableIntStateOf(0) }
    val pages = remember { sampleAnimalPages() }      // 1P: 8종, 2P: SHEEP + LOCK 7
    val pageCount = pages.size
    val baseSlots = pages[currentPage].slots          // 현재 페이지의 기본 슬롯(잠금 상태)

    // 3) 앨범 실제 렌더 크기 측정(이미지는 건드리지 않고, 그 위에 동일 크기 박스를 얹기 위함)
    val density = LocalDensity.current
    var albumPxSize by remember { mutableStateOf(IntSize.Zero) }

    // 4) unlock 덮어쓰기 (서버 상태 기반으로 잠금/이미지 반영)
    val displaySlots = remember(baseSlots, ui.unlockMap) {
        baseSlots.map { slot ->
            val type = slot.id.toAnimalTypeOrNull()
            if (type == null) {
                slot // LOCK* 같은 자리 채움
            } else {
                val opened = ui.unlockMap.containsKey(type)
                slot.copy(
                    unlocked = opened,
                    imageRes = if (opened) type.toImageRes() else null
                )
            }
        }
    }


    // 5) 레이아웃
    Box(Modifier.fillMaxSize()) {

        // 배경
        Image(
            painter = painterResource(R.drawable.mypage_bg),
            contentDescription = "MyPage background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 닫기(뒤로가기) 버튼 - 앨범 우상단
        Image(
            painter = painterResource(R.drawable.close_btn),
            contentDescription = "닫기",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 28.dp, end = 36.dp)
                .size(56.dp)
                .clickable { onBackClick() },
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 중앙 앨범 배경 (사이즈는 이미 맞춰둔 것 그대로)
        Image(
            painter = painterResource(R.drawable.animal_album),
            contentDescription = "Animal Book Background",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.93f)
                .onGloballyPositioned { albumPxSize = it.size }, // ← 렌더된 실제 크기 읽기
            contentScale = ContentScale.Fit
        )

        // 좌/우 페이지 화살표 (상세 중에는 숨김)
        if (ui.selected == null && currentPage > 0) {
            Image(
                painter = painterResource(R.drawable.previous_btn),
                contentDescription = "이전",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(80.dp)
                    .padding(start = 24.dp)
                    .clickable { currentPage-- }
            )
        }
        if (ui.selected == null && currentPage < pageCount - 1) {
            Image(
                painter = painterResource(R.drawable.next_btn),
                contentDescription = "다음",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(80.dp)
                    .padding(end = 24.dp)
                    .clickable { currentPage++ }
            )
        }

        // 내부 종이영역 패딩(리소스 맞게 조정)
        val innerLeft = 190.dp
        val innerRight = 200.dp
        val innerTop = 130.dp
        val innerBottom = 120.dp
        val itemSpacing = 16.dp

        // 좌우만 ‘조금’ 넓히기: 줄일 패딩값 (한쪽당)
        val widenPerSide = 20.dp

        val padStart = (innerLeft - widenPerSide).coerceAtLeast(0.dp)
        val padEnd = (innerRight - widenPerSide).coerceAtLeast(0.dp)

        // 6) 앨범과 "동일한 크기"의 오버레이 컨테이너 위에 그리드/상세를 얹음
        if (albumPxSize.height > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.93f)                                 // 가로: 앨범과 동일
                    .height(with(density) { albumPxSize.height.toDp() }) // 세로: 앨범과 동일
                    .padding(
                        start = padStart,
                        end = padEnd,
                        top = innerTop,
                        bottom = innerBottom
                    )
                    .zIndex(1f)
            ) {
                AnimatedContent(
                    targetState = ui.selected,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "animal-book"
                ) { selected ->
                    if (selected == null) {
                        // ─ 그리드(4×2=8칸) ─
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(
                                itemSpacing,
                                Alignment.CenterHorizontally
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                itemSpacing,
                                Alignment.CenterVertically
                            ),
                            userScrollEnabled = false,
                            modifier = Modifier.fillMaxSize() // 앨범 내부 전체
                        ) {
                            items(items = displaySlots, key = { it.id }) { slot ->
                                val type = slot.id.toAnimalTypeOrNull()
                                AnimalSlotItem(
                                    slot = slot,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (type != null && slot.unlocked) {
                                        viewModel.onSelect(type) // 상세 전환 + 스토리 로딩
                                    }
                                }
                            }
                        }
                    } else {
                        val sel = selected
                        AnimalSpecPane(
                            animalImageRes = sel.animal.toImageRes(),
                            story = sel.story ?: "",
                            onBack = { viewModel.closeDetail() },
                            onTakePhoto = { onAnimalSpecClick(sel.animal.name) } // ★ 여기만 교체
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// 컴포넌트: AnimalSlotItem (타일 전체 클릭, 리플 제거)
// ──────────────────────────────────────────────────────────────
@Composable
fun AnimalSlotItem(
    slot: AnimalSlot,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val enabled = slot.unlocked
    Box(
        modifier
            .aspectRatio(1f) // 셀 정사각
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFBBF5E)) // 잠금/해제 동일 배경(필요 시 분기)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 리플 제거
            ) { onClick() }
            .padding(8.dp)
            .semantics { contentDescription = slot.name },
        contentAlignment = Alignment.Center
    ) {
        if (slot.imageRes != null) {
            Image(
                painter = painterResource(slot.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.9f),
                contentScale = ContentScale.Fit
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.locked_sign),
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = Color(0xFF743D06)
            )
        }
    }
}


// ──────────────────────────────────────────────────────────────
// AnimalSpecPane (상세 페이지)
//  - 동물 이미지 + 스토리 + 사진찍기 버튼
//  - 뒤로가기 버튼 클릭 시 그리드로 복귀
// ──────────────────────────────────────────────────────────────
// 상세 페이지
@Composable
private fun AnimalSpecPane(
    animalImageRes: Int,
    story: String,
    onBack: () -> Unit,
    onTakePhoto: () -> Unit
) {
    // 앨범 내부(오버레이 박스) 전체를 사용
    Box(
        Modifier
            .fillMaxSize()
            .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 10.dp) // 앨범 내부 여백
    ) {

        // 왼쪽 상단 Back 버튼
        Image(
            painter = painterResource(R.drawable.back_btn),
            contentDescription = "뒤로",
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(52.dp)
                .clickable { onBack() }
        )

        // 양쪽 페이지 레이아웃
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp), // 중앙 접힌선 간 살짝 여유
            horizontalArrangement = Arrangement.spacedBy(16.dp),   // 두 컬럼 사이 여백
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽 페이지: 동물 이미지 + 버튼
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(animalImageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.height(24.dp))

                // 사진 찍기 버튼
                BaseButton(
                    text = "함께 사진 남기기",
                    onClick = onTakePhoto
                )
            }

            // 오른쪽 페이지: 스토리
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                StoryText(text = story)
            }
        }
    }
}

// 보조 컴포넌트 (스토리 텍스트)
@Composable
private fun StoryText(text: String) {
    val displayText = remember(text) { normalizeBreaks(text) }

    Log.d("TEXT", "raw=[$text]")
    Log.d(
        "TEXT",
        "display=[$displayText], hasRealNL=${displayText.any { it == '\n' }}, hasLiteral\\n=${
            displayText.contains("\\n")
        }"
    )

    val scroll = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,              // ✅ 변환된 문자열 사용
            color = Color(0xFF5C3B1E),
            fontSize = 30.sp,
            lineHeight = 44.sp,
            textAlign = TextAlign.Center,
            softWrap = true,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(min = 0.dp, max = 360.dp)
                .background(Color.Transparent)
                .padding(4.dp)
                .verticalScroll(scroll),
        )
    }
}


// ──────────────────────────────────────────────────────────────
/** 유틸
 * 1. 문자열 → AnimalType (대소문자/공백 방어)
 * 2. 1) JSON 이스케이프 형태: "\r\n" / "\n" (둘 다 백슬래시 포함) → 실제 개행
 *    2) 혹시 진짜 CRLF/CR로 들어온 것도 LF로 통일
 * */
// ──────────────────────────────────────────────────────────────
private fun String.toAnimalTypeOrNull() =
    runCatching { AnimalType.valueOf(trim().uppercase()) }.getOrNull()

private fun normalizeBreaks(s: String): String {
    return s.replace("\\r\\n", "\n")
        .replace("\\n", "\n")
        .replace("\r\n", "\n")
        .replace("\r", "\n")
}

// ──────────────────────────────────────────────────────────────
/** 고정 스테이지 순서 + 레이아웃(8칸/페이지) */
//  - 초기엔 전부 잠금
//  - unlock 시 표시할 이미지 리소스는 imageOf(id)에서 매핑
// ──────────────────────────────────────────────────────────────
private val StageOrder = listOf(
    "TURTLE", "DOG", "RABBIT", "SWAN", "DOLPHIN", "CRANE", "BEAR", "PARROT", "SHEEP"
)


// ──────────────────────────────────────────────────────────────
/** 샘플 페이지 데이터 생성 */
//  - 그리드의 자리(배치)만 미리 만들어두는 더미 데이터
//  - 실제 앱에서는 서버에서 받아온 unlock 상태에 따라 이미지 리소스가 채워짐
//  - 페이지 1: 앞 8종 (TURTLE ~ PARROT)
//  - 페이지 2: SHEEP + LOCK 7개
// ──────────────────────────────────────────────────────────────

private fun sampleAnimalPages(): List<AnimalPage> {
    val first = StageOrder.take(8).map { id ->
        AnimalSlot(id = id, name = id, unlocked = false, imageRes = null) // ← 이미지 넣지 않음
    }
    val second = buildList {
        add(AnimalSlot(id = StageOrder[8], name = StageOrder[8], unlocked = false, imageRes = null))
        repeat(7) { idx ->
            add(AnimalSlot(id = "LOCK${idx + 1}", name = "잠금", unlocked = false, imageRes = null))
        }
    }
    return listOf(AnimalPage(first), AnimalPage(second))
}
