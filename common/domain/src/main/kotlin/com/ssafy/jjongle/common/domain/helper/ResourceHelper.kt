package com.ssafy.jjongle.common.domain.helper

/**
 * Resource Helper는 여러 계층에서 반복되는 작업을 추상화한 helper 계약입니다.
 *
 * 호출자는 Android framework나 Compose 구현을 직접 알지 않고 필요한 동작만 요청합니다.
 */
interface ResourceHelper {
    /**
     * Android string resource id에 해당하는 문자열을 반환합니다.
     */
    fun getString(id: Int): String
}
