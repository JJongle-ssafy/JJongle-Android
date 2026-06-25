package com.ssafy.jjongle.common.presentation.message

import com.ssafy.jjongle.common.domain.helper.MessageHelper
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 공통 기반 흐름에서 허용되는 Message Effect 값의 집합입니다.
 *
 * 분기 가능한 상태나 이벤트를 타입으로 제한해 잘못된 문자열/숫자 값이 계층 사이로 전달되지 않게 합니다.
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
 * Snackbar나 Dialog처럼 한 번만 소비되어야 하는 전역 메시지 효과를 전달하는 버스입니다.
 *
 * domain 계층의 MessageHelper 호출을 Compose 표시와 분리해, 화면 전환 중에도 메시지 이벤트를 앱 루트에서 일관되게 처리합니다.
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
