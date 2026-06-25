package com.ssafy.jjongle.presentation.ui.mapper

import androidx.annotation.DrawableRes
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.common.entity.AnimalType

/**
 * Animal Type는 메인에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
@DrawableRes
fun AnimalType.toImageRes(): Int = when (this) {
    AnimalType.TURTLE -> R.drawable.turtle
    AnimalType.DOG -> R.drawable.dog
    AnimalType.RABBIT -> R.drawable.rabbit
    AnimalType.SWAN -> R.drawable.swan
    AnimalType.DOLPHIN -> R.drawable.dolphin
    AnimalType.CRANE -> R.drawable.crane
    AnimalType.BEAR -> R.drawable.bear
    AnimalType.PARROT -> R.drawable.parrot
    AnimalType.SHEEP -> R.drawable.sheep
}
