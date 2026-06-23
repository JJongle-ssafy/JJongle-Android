package com.ssafy.jjongle.common.entity

data class GameFinishEvent(
    val profiles: List<GameProfileImage>
) : GameEvent
