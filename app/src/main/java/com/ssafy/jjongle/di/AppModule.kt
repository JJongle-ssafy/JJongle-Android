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
 * App 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
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
