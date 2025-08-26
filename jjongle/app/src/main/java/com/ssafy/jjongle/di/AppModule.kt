package com.ssafy.jjongle.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ssafy.jjongle.data.repository.SettingsRepositoryImpl
import com.ssafy.jjongle.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Binds

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("jjongle_prefs", Context.MODE_PRIVATE)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}