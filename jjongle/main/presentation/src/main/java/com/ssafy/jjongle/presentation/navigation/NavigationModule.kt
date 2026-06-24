package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun bindNavigationHelper(
        appNavigationHelper: AppNavigationHelper,
    ): NavigationHelper
}
