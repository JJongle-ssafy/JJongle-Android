package com.ssafy.jjongle.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.jjongle.data.service.BgmManager
import com.ssafy.jjongle.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext ctx: Context): ExoPlayer =
        ExoPlayer.Builder(ctx).build().apply {
            val attrs = AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
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
        @ApplicationContext ctx: Context,
        player: ExoPlayer,
        settingsRepository: SettingsRepository
    ): BgmManager = BgmManager(ctx, player, settingsRepository)
}
