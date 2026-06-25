package com.ssafy.jjongle.oxgame.data.di

import android.content.Context
import androidx.room.Room
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryRepository
import com.ssafy.jjongle.oxgame.domain.repository.OXGameRepository
import com.ssafy.jjongle.oxgame.domain.repository.OXQuizRepository
import com.ssafy.jjongle.oxgame.data.local.JjongleDatabase
import com.ssafy.jjongle.oxgame.data.local.OXGameHistoryDao
import com.ssafy.jjongle.oxgame.data.repository.LocalOXQuizRepositoryImpl
import com.ssafy.jjongle.oxgame.data.repository.OXGameHistoryRepositoryImpl
import com.ssafy.jjongle.oxgame.data.repository.OXGameRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * OXGame Data 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OXGameDataModule {

    @Binds
    @Singleton
    abstract fun bindOXGameRepository(
        oxGameRepositoryImpl: OXGameRepositoryImpl
    ): OXGameRepository

    @Binds
    @Singleton
    abstract fun bindOXQuizRepository(
        impl: LocalOXQuizRepositoryImpl
    ): OXQuizRepository

    @Binds
    @Singleton
    abstract fun bindOXGameHistoryRepository(
        impl: OXGameHistoryRepositoryImpl
    ): OXGameHistoryRepository
}

/**
 * OXGame Room 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object OXGameRoomModule {

    @Provides
    @Singleton
    fun provideJjongleDatabase(
        @ApplicationContext context: Context,
    ): JjongleDatabase =
        Room.databaseBuilder(
            context,
            JjongleDatabase::class.java,
            "jjongle.db",
        ).build()

    @Provides
    @Singleton
    fun provideOXGameHistoryDao(database: JjongleDatabase): OXGameHistoryDao =
        database.oxGameHistoryDao()
}
