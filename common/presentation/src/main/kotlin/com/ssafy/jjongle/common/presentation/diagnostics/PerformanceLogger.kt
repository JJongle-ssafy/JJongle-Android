package com.ssafy.jjongle.common.presentation.diagnostics

/**
 * interface는 공통에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
fun interface PerformanceLogger {
    fun log(tag: String, message: String)

    companion object {
        val NoOp = PerformanceLogger { _, _ -> }
    }
}
