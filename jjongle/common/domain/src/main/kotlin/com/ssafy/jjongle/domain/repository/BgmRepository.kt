package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.BgmGroup

interface BgmRepository {
    fun playFor(group: BgmGroup)
    fun pause()
    fun resume()
    fun stop()
}
