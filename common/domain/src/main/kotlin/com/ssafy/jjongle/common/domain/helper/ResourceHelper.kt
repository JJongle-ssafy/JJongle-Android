package com.ssafy.jjongle.common.domain.helper

/**
 * ResourceHelper 관련 도메인 작업을 보조하는 컴포넌트입니다.
 *
 * - 계층: common/domain
 * - 책임: 반복되는 판단, 변환, 계산 로직을 별도 책임으로 분리합니다.
 */
interface ResourceHelper {
    /**
     * Android string resource id에 해당하는 문자열을 반환합니다.
     */
    fun getString(id: Int): String
}
