package com.ssafy.jjongle.common.presentation.diagnostics

/**
 * interface 기능에서 사용하는 최상위 헬퍼입니다.
 *
 * - 계층: common/presentation
 * - 책임: 파일의 대표 작업을 함수 단위로 분리해 호출 지점을 단순하게 유지합니다.
 */
fun interface PerformanceLogger {
    fun log(tag: String, message: String)

    companion object {
        val NoOp = PerformanceLogger { _, _ -> }
    }
}
