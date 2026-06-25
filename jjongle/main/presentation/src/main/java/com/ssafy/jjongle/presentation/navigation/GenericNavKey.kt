package com.ssafy.jjongle.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * GenericNavKey Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: main/presentation
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
 */
@Serializable
data class GenericNavKey(
    val path: String,
    val args: Map<String, String> = emptyMap(),
) : NavKey
