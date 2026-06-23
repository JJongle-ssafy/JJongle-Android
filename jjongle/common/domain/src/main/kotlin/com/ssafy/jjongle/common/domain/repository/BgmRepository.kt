package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.BgmGroup

interface BgmRepository {
    fun playFor(group: BgmGroup)
    fun pause()
    fun resume()
    fun stop()
}
