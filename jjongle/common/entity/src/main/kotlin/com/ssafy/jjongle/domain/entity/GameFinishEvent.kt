package com.ssafy.jjongle.domain.entity

data class GameFinishEvent(
    val profiles: List<GameProfileImage>
) : GameEvent
