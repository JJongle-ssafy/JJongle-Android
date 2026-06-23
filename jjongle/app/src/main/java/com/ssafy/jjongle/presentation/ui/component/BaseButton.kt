package com.ssafy.jjongle.presentation.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.jjongle.presentation.ui.theme.JjongleBrown
import com.ssafy.jjongle.presentation.ui.theme.JjongleWhite

@Composable
fun BaseButton(
    onClick: () -> Unit,
    text: String,
    fontSize: TextUnit = 30.sp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp), // 둥근 모서리 모양을 적용합니다.
        colors = ButtonDefaults.buttonColors(
            containerColor = JjongleBrown, // 버튼의 배경색
            contentColor = JjongleWhite,   // 버튼 내용(텍스트)의 색
            disabledContainerColor = Color.Gray, // 비활성화된 버튼의 배경색
            disabledContentColor = Color.White.copy(alpha = 0.6f) // 비활성화된 버튼의 텍스트 색
        ),
        enabled = enabled,
        // 버튼의 그림자(Elevation)를 없애 평평하게 만듭니다.
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        // 버튼 내부의 여백(padding)을 조절합니다.
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold // 글자를 굵게 처리합니다.
        )
    }
}

// Android Studio 미리보기에서 버튼 모양을 바로 확인할 수 있습니다.
@Preview(showBackground = true)
@Composable
fun BaseButtonPreview() {
    BaseButton(
        onClick = { /*TODO*/ },
        text = "처음으로 돌아가기"
    )
}
