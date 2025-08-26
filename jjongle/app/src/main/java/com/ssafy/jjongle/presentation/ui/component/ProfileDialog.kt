package com.ssafy.jjongle.presentation.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.jjongle.presentation.model.CharacterType

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
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 갈색 박스 (배경 카드)
        // TODO: 전체적으로 사이즈 키워도 될 것 같음 !
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = Color(0xFF5C2B2A), shape = RoundedCornerShape(12.dp))
                .widthIn(max = 400.dp)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 닉네임 설정
                Text(
                    text = "닉네임 설정",
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = nickname,
                    onValueChange = { onNicknameChange(it.take(10)) },      // 최대 10자 제한
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    ),
                    placeholder = {
                        Text(text = placeholderText ?: "닉네임을 입력하세요", color = Color.LightGray)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF8C5F60),
                        unfocusedContainerColor = Color(0xFF8C5F60),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true,
                    // 10자 제한
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 캐릭터 선택
                Text(
                    text = "캐릭터 선택",
                    color = Color.White
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
                                    color = if (isSelected) Color.Yellow else Color.White,
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A934C)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(confirmText)
                }
            }
        }
    }
}
