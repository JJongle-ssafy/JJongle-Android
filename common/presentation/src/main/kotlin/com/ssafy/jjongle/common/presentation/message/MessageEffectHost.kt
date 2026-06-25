package com.ssafy.jjongle.common.presentation.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import kotlinx.coroutines.flow.collectLatest

/**
 * MessageEffectHost Compose UI를 구성합니다.
 *
 * - 계층: common/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
@Composable
fun MessageEffectHost(
    messageEffectBus: MessageEffectBus,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var dialogEffect by remember { mutableStateOf<MessageEffect?>(null) }

    LaunchedEffect(messageEffectBus) {
        messageEffectBus.effects.collectLatest { effect ->
            when (effect) {
                is MessageEffect.SnackBar -> snackbarHostState.showSnackbar(effect.message)
                is MessageEffect.OneButtonDialog,
                is MessageEffect.TwoButtonDialog -> dialogEffect = effect
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        content()
        SnackbarHost(hostState = snackbarHostState)
        dialogEffect?.let { effect ->
            MessageDialog(
                effect = effect,
                onDismiss = { dialogEffect = null },
            )
        }
    }
}

@Composable
private fun MessageDialog(
    effect: MessageEffect,
    onDismiss: () -> Unit,
) {
    when (effect) {
        is MessageEffect.OneButtonDialog -> AlertDialog(
            onDismissRequest = {
                if (!effect.cantIgnore) onDismiss()
            },
            text = { ArchiText(text = effect.description) },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        effect.onClickButton()
                    },
                ) {
                    ArchiText(text = "확인")
                }
            },
        )

        is MessageEffect.TwoButtonDialog -> AlertDialog(
            onDismissRequest = {
                onDismiss()
                effect.onClickNegative()
            },
            text = { ArchiText(text = effect.description) },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        effect.onClickPositive()
                    },
                ) {
                    ArchiText(text = "확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        effect.onClickNegative()
                    },
                ) {
                    ArchiText(text = "취소")
                }
            },
        )

        is MessageEffect.SnackBar -> Unit
    }
}
