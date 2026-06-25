package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.BgmGroup

/**
 * BgmRepository domain 계층이 의존하는 저장소 계약입니다.
 *
 * - 계층: common/domain
 * - 책임: data 구현을 숨기고 유스케이스에 필요한 작업만 노출합니다.
 */
interface BgmRepository {
    fun playFor(group: BgmGroup)
    fun pause()
    fun resume()
    fun stop()
}
