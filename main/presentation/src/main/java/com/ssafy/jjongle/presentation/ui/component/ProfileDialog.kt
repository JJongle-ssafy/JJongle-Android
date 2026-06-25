package com.ssafy.jjongle.common.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.presentation.model.CharacterType

/**
 * ProfileDialog Compose UI를 구성합니다.
 *
 * - 계층: main/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
@Composable
fun ProfileDialog(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    selectedCharacter: CharacterType,
    onCharacterSelect: (CharacterType) -> Unit,
    onConfirmClick: () -> Unit,
    confirmText: String = "가입하기",   // 기본은 “가입하기”
    placeholderText: String? = null            // ← 추가
) {
    val colors = ArchiThemeImpl.archiColor
    val typeScale = ArchiThemeImpl.typeScale

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 갈색 박스 (배경 카드)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = colors.bgBrandLevel0, shape = RoundedCornerShape(12.dp))
                .widthIn(max = 400.dp)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 닉네임 설정
                ArchiText(
                    text = "닉네임 설정",
                    style = typeScale.textStrongM,
                    color = colors.contentOnBrand,
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = nickname,
                    onValueChange = { onNicknameChange(it.take(10)) },      // 최대 10자 제한
                    textStyle = typeScale.textRegularM.copy(color = colors.contentOnBrand),
                    placeholder = {
                        ArchiText(
                            text = placeholderText ?: "닉네임을 입력하세요",
                            style = typeScale.textRegularM,
                            color = colors.borderDefaultLevel0,
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.contentDefaultLevel1,
                        unfocusedContainerColor = colors.contentDefaultLevel1,
                        focusedIndicatorColor = colors.borderAccent,
                        unfocusedIndicatorColor = colors.borderDefaultLevel0,
                    ),
                    singleLine = true,
                    // 10자 제한
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 캐릭터 선택
                ArchiText(
                    text = "캐릭터 선택",
                    style = typeScale.textStrongM,
                    color = colors.contentOnBrand,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CharacterType.values().forEach { character ->
                        val isSelected = character == selectedCharacter

                        Image(
                            painter = painterResource(id = character.profileImageRes),
                            contentDescription = character.displayName,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 4.dp else 2.dp,
                                    color = if (isSelected) colors.contentAccent else colors.contentOnBrand,
                                    shape = CircleShape
                                )
                                .clickable { onCharacterSelect(character) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 가입하기 버튼
                Button(
                    onClick = onConfirmClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.borderAccent,
                        contentColor = colors.contentOnBrand,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    ArchiText(
                        text = confirmText,
                        style = typeScale.textStrongM,
                        color = colors.contentOnBrand,
                    )
                }
            }
        }
    }
}
