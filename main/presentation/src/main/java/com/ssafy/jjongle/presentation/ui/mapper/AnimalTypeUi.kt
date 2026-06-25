package com.ssafy.jjongle.presentation.ui.mapper

import androidx.annotation.DrawableRes
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.common.entity.AnimalType

/**
 * AnimalType Compose UI를 구성합니다.
 *
 * - 계층: main/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
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
