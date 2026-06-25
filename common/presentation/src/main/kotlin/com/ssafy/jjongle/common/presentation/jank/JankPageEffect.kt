package com.ssafy.jjongle.common.presentation.jank

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState

/**
 * Compose 화면이 표시되는 동안 JankStats 관찰 대상을 현재 페이지로 연결하는 side effect입니다.
 *
 * 화면 진입과 이탈에 맞춰 페이지 태그를 갱신해 jank 로그가 어떤 화면에서 발생했는지 추적합니다.
 */
@Composable
fun JankPageEffect(
    pageName: String,
    reporter: JankReporter? = null,
) {
    val activeReporter = reporter ?: LocalJankReporter.current
    val view = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = remember(view) { view.context.findActivity() }

    DisposableEffect(view, lifecycleOwner, activity, pageName, activeReporter) {
        if (activity == null) {
            return@DisposableEffect onDispose { }
        }

        val metricsStateHolder = PerformanceMetricsState.getHolderForHierarchy(view)
        val jankStats = runCatching {
            JankStats.createAndTrack(activity.window) { frameData ->
                activeReporter.report(
                    JankFrameData(
                        pageName = pageName,
                        isJank = frameData.isJank,
                        frameDurationUiNanos = frameData.frameDurationUiNanos,
                        frameStartNanos = frameData.frameStartNanos,
                    ),
                )
            }
        }.getOrNull()

        metricsStateHolder.state?.putState("Page", pageName)

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> jankStats?.isTrackingEnabled = true
                Lifecycle.Event.ON_PAUSE -> {
                    activeReporter.flush(JankReportReason.PAGE_EXIT, pageName)
                    jankStats?.isTrackingEnabled = false
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            metricsStateHolder.state?.removeState("Page")
            lifecycleOwner.lifecycle.removeObserver(observer)
            activeReporter.flush(JankReportReason.PAGE_EXIT, pageName)
            jankStats?.isTrackingEnabled = false
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
