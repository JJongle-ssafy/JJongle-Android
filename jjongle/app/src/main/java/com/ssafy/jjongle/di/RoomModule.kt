package com.ssafy.jjongle.di

import android.content.Context
import androidx.room.Room
import com.ssafy.jjongle.data.local.JjongleDatabase
import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideJjongleDatabase(
        @ApplicationContext context: Context
    ): JjongleDatabase {
        return Room.databaseBuilder(
            context,
            JjongleDatabase::class.java,
            "jjongle.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideOXGameHistoryDao(database: JjongleDatabase): OXGameHistoryDao {
        return database.oxGameHistoryDao()
    }
}
