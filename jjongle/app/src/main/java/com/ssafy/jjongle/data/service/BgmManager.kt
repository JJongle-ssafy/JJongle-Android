package com.ssafy.jjongle.data.service

import android.content.Context
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.jjongle.R
import com.ssafy.jjongle.domain.repository.SettingsRepository
import com.ssafy.jjongle.presentation.media.BgmGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BgmManager @Inject constructor(
    private val app: Context,
    private val player: ExoPlayer,
    private val settingsRepository: SettingsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var currentGroup: BgmGroup? = null
    private var fadeJob: Job? = null

    @OptIn(UnstableApi::class)
    private fun uri(@RawRes resId: Int) =
        RawResourceDataSource.buildRawResourceUri(resId)

    private fun mediaItemFor(group: BgmGroup): MediaItem = when (group) {
        BgmGroup.WORLD -> MediaItem.fromUri(uri(R.raw.bgm_world))
        BgmGroup.TANGRAM -> MediaItem.fromUri(uri(R.raw.bgm_tangram))
        BgmGroup.OX -> MediaItem.fromUri(uri(R.raw.bgm_ox))
        BgmGroup.MYPAGE -> MediaItem.fromUri(uri(R.raw.bgm_mypage))
    }

    fun playFor(group: BgmGroup) {
        scope.launch {
            val bgmEnabled = settingsRepository.getBgmEnabled().first()
            if (!bgmEnabled) return@launch
            
            if (group == currentGroup && player.isPlaying) return@launch // 동일 그룹이면 유지
            currentGroup = group
            crossfadeTo(mediaItemFor(group))
        }
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun resume() {
        player.playWhenReady = true
    }

    fun stop() {
        player.stop()
    }

    private fun crossfadeTo(item: MediaItem, durationMs: Long = 800) {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            // 1) 페이드아웃
            fadeVolume(from = player.volume, to = 0f, durationMs = durationMs / 2)
            // 2) 트랙 교체
            player.setMediaItem(item, /* resetPosition= */ true)
            player.prepare()
            player.playWhenReady = true
            player.volume = 0f
            // 3) 페이드인
            fadeVolume(from = 0f, to = 1f, durationMs = durationMs / 2)
        }
    }

    private suspend fun fadeVolume(from: Float, to: Float, durationMs: Long) {
        val steps = 12
        val stepTime = (durationMs / steps).coerceAtLeast(1)
        for (i in 0..steps) {
            val t = i / steps.toFloat()
            player.volume = from + (to - from) * t
            delay(stepTime)
        }
    }
}
