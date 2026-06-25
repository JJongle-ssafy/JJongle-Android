package com.ssafy.jjongle.di

import android.content.Context
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.common.presentation.diagnostics.PerformanceLogger
import com.ssafy.jjongle.common.presentation.jank.JankReporter
import com.ssafy.jjongle.common.presentation.jank.LoggingJankReporter
import com.ssafy.jjongle.common.presentation.message.MessageEffectBus
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tti.TTIHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: app
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideResourceHelper(@ApplicationContext context: Context): ResourceHelper =
        object : ResourceHelper {
            override fun getString(id: Int): String = context.getString(id)
        }

    @Provides
    @Singleton
    fun provideMessageEffectBus(): MessageEffectBus = MessageEffectBus()

    @Provides
    @Singleton
    fun provideMessageHelper(messageEffectBus: MessageEffectBus): MessageHelper = messageEffectBus

    @Provides
    @Singleton
    fun provideJankReporter(): JankReporter = LoggingJankReporter(
        logger = { message -> PerformanceLogger.NoOp.log("JankStats", message) },
    )

    @Provides
    @Singleton
    fun provideTTIHelper(): TTIHelper = TTIHelperImpl(
        logger = { message -> PerformanceLogger.NoOp.log("TTI", message) },
    )
}
