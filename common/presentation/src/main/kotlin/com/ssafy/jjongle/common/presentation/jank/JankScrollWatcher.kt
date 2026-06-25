package com.ssafy.jjongle.common.presentation.jank

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun JankScrollWatcher(
    pageName: String,
    scrollableState: ScrollableState,
    reporter: JankReporter? = null,
) {
    val activeReporter = reporter ?: LocalJankReporter.current
    val scrollSession = remember(scrollableState) { ScrollSessionState() }

    LaunchedEffect(pageName, scrollableState, activeReporter) {
        snapshotFlow { scrollableState.isScrollInProgress }
            .distinctUntilChanged()
            .map { isInProgress ->
                if (isInProgress) {
                    scrollSession.hasScrolled = true
                }
                isInProgress
            }
            .drop(1)
            .filter { isInProgress -> !isInProgress && scrollSession.hasScrolled }
            .collect {
                scrollSession.hasScrolled = false
                activeReporter.flush(JankReportReason.SCROLL_END, pageName)
            }
    }
}

/**
 * 공통 기반 화면을 렌더링하는 데 필요한 값을 담는 상태 스냅샷입니다.
 *
 * 여러 값을 화면에서 따로 수집하지 않도록 한 모델로 묶어, 상태 변경 지점을 ViewModel 안에서 추적할 수 있게 합니다.
 */
private class ScrollSessionState {
    var hasScrolled: Boolean = false
}
