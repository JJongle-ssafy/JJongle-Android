package com.ssafy.jjongle.common.data.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.jjongle.common.data.service.BgmManager
import com.ssafy.jjongle.common.domain.repository.BgmRepository
import com.ssafy.jjongle.common.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AudioDataModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: common/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object AudioDataModule {

    @Provides
    @Singleton
    @OptIn(UnstableApi::class)
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer =
        ExoPlayer.Builder(context).build().apply {
            val attrs = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_GAME)
                .build()
            setAudioAttributes(attrs, /* handleAudioFocus= */ true)
            setHandleAudioBecomingNoisy(true)
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 1.0f
        }

    @Provides
    @Singleton
    fun provideBgmManager(
        @ApplicationContext context: Context,
        player: ExoPlayer,
        settingsRepository: SettingsRepository,
    ): BgmRepository = BgmManager(context, player, settingsRepository)
}
