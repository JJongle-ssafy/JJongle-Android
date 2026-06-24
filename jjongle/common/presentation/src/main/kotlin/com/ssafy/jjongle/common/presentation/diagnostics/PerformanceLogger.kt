package com.ssafy.jjongle.common.presentation.diagnostics

fun interface PerformanceLogger {
    fun log(tag: String, message: String)

    companion object {
        val NoOp = PerformanceLogger { _, _ -> }
    }
}
