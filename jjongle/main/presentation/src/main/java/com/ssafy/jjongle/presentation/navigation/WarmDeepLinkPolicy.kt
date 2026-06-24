package com.ssafy.jjongle.presentation.navigation

fun List<GenericNavKey>.bringToFront(target: GenericNavKey): List<GenericNavKey> =
    filterNot { it == target } + target
