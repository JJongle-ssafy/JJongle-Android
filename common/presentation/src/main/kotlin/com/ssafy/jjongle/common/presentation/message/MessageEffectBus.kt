package com.ssafy.jjongle.common.presentation.message

import com.ssafy.jjongle.common.domain.helper.MessageHelper
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * MessageEffect 모듈 기능을 표현하는 interface 선언입니다.
 *
 * - 계층: common/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
sealed interface MessageEffect {
    /**
     * Snackbar로 노출할 짧은 메시지입니다.
     */
    data class SnackBar(val message: String) : MessageEffect

    /**
     * 버튼 하나를 가진 Dialog 메시지입니다.
     */
    data class OneButtonDialog(
        val cantIgnore: Boolean,
        val description: String,
        val onClickButton: () -> Unit,
    ) : MessageEffect

    /**
     * 긍정/부정 버튼을 가진 Dialog 메시지입니다.
     */
    data class TwoButtonDialog(
        val description: String,
        val onClickPositive: () -> Unit,
        val onClickNegative: () -> Unit,
    ) : MessageEffect
}

/**
 * MessageEffectBus 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: common/presentation
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
class MessageEffectBus : MessageHelper {
    private val mutableEffects = MutableSharedFlow<MessageEffect>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * 화면 host가 구독하는 일회성 메시지 효과 스트림입니다.
     */
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
