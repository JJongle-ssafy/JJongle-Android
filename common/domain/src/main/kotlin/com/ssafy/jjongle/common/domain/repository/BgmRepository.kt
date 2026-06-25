package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.BgmGroup

/**
 * Bgm 기능이 domain 계층에서 기대하는 저장소 계약입니다.
 *
 * UseCase는 이 계약에만 의존하고, Firebase, Room, Retrofit 같은 실제 데이터 구현은 data 계층에 숨깁니다.
 */
interface BgmRepository {
    fun playFor(group: BgmGroup)
    fun pause()
    fun resume()
    fun stop()
}
