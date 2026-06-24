package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

data class GameFinishEvent(
    val profiles: ImmutableList<GameProfileImage>
) : GameEvent
