package com.ssafy.jjongle.common.data.di

import android.content.Context
import android.content.SharedPreferences
import com.ssafy.jjongle.common.data.repository.SettingsRepositoryImpl
import com.ssafy.jjongle.common.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageProviderModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("jjongle_prefs", Context.MODE_PRIVATE)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageDataModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl,
    ): SettingsRepository
}
