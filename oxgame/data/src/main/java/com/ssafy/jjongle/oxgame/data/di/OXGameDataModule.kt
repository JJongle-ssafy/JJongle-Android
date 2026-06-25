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
 * OXGameDataModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: oxgame/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
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
 * OXGameRoomModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: oxgame/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
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
