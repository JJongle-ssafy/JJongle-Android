package com.ssafy.jjongle.common.presentation.message

import com.ssafy.jjongle.common.domain.helper.MessageHelper
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface MessageEffect {
    data class SnackBar(val message: String) : MessageEffect

    data class OneButtonDialog(
        val cantIgnore: Boolean,
        val description: String,
        val onClickButton: () -> Unit,
    ) : MessageEffect

    data class TwoButtonDialog(
        val description: String,
        val onClickPositive: () -> Unit,
        val onClickNegative: () -> Unit,
    ) : MessageEffect
}

class MessageEffectBus : MessageHelper {
    private val mutableEffects = MutableSharedFlow<MessageEffect>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val effects: SharedFlow<MessageEffect> = mutableEffects.asSharedFlow()

    override fun showToast(messageText: String) {
        showSnackBar(messageText)
    }

    override fun showSnackBar(messageText: String) {
        mutableEffects.tryEmit(MessageEffect.SnackBar(messageText))
    }

    override fun showOneButtonDialog(
        cantIgnore: Boolean,
        descText: String,
        onClickButton: () -> Unit,
    ) {
        mutableEffects.tryEmit(
            MessageEffect.OneButtonDialog(
                cantIgnore = cantIgnore,
                description = descText,
                onClickButton = onClickButton,
            )
        )
    }

    override fun showTwoButtonDialog(
        descText: String,
        onClickPositive: () -> Unit,
        onClickNegative: () -> Unit,
    ) {
        mutableEffects.tryEmit(
            MessageEffect.TwoButtonDialog(
                description = descText,
                onClickPositive = onClickPositive,
                onClickNegative = onClickNegative,
            )
        )
    }
}
