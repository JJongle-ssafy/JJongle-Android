package com.ssafy.jjongle.presentation.ui.mapper

import androidx.annotation.DrawableRes
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.common.entity.AnimalType

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
