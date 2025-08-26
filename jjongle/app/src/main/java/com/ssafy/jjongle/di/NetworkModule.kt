package com.ssafy.jjongle.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.data.remote.AuthApiService
import com.ssafy.jjongle.data.remote.AuthInterceptor
import com.ssafy.jjongle.data.remote.OXGameApiService
import com.ssafy.jjongle.data.remote.PrettyHttpLoggingInterceptor
import com.ssafy.jjongle.data.remote.SuperToneApiInterceptor
import com.ssafy.jjongle.data.remote.SuperToneApiService
import com.ssafy.jjongle.data.remote.TangramGameApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authDataSource: AuthDataSource,
        @Named("refresh") api: AuthApiService
    ): AuthInterceptor = AuthInterceptor(authDataSource, api)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val prettyLoggingInterceptor = PrettyHttpLoggingInterceptor()
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(prettyLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://i13d106.p.ssafy.io:8080/") // 실제 API URL로 변경 필요
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshOkHttpClient(): OkHttpClient {
        val pretty = PrettyHttpLoggingInterceptor()
        return OkHttpClient.Builder()
            // ✅ 재발급 경로에 남아 있던 Authorization 헤더를 강제로 제거
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .removeHeader("Authorization")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(pretty)
            .build()
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshRetrofit(
        @Named("refresh") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl("http://i13d106.p.ssafy.io:8080/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshAuthApiService(
        @Named("refresh") retrofit: Retrofit
    ): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideOXGameApiService(retrofit: Retrofit): OXGameApiService {
        return retrofit.create(OXGameApiService::class.java)
    }

    // 칠교 게임 API 서비스 제공
    @Provides
    @Singleton
    fun provideTangramGameApiService(retrofit: Retrofit): TangramGameApiService {
        return retrofit.create(TangramGameApiService::class.java)
    }

    // SuperTone API 키 제공
    @Provides
    @Singleton
    @Named("superToneApiKey")
    fun provideSuperToneApiKey(): String {
        return "6fa5959a460900b679a2b4e110b2b2e7" // 승훈키
//        return "93a2b3a3ca272b1aa473e1634d7c61e6" // 소영키
//        return "c66455e3035224beee7cf202c2eea030" // 동근키
        return "YOUR_SUPERTONE_API_KEY" // 실제 API 키로 변경 필요
    }

    // SuperTone API 인터셉터 제공
    @Provides
    @Singleton
    fun provideSuperToneApiInterceptor(
        @Named("superToneApiKey") apiKey: String
    ): SuperToneApiInterceptor {
        return SuperToneApiInterceptor(apiKey)
    }

    // SuperTone API용 OkHttpClient 제공
    @Provides
    @Singleton
    @Named("SuperToneOkHttpClient")
    fun provideSuperToneOkHttpClient(
        superToneApiInterceptor: SuperToneApiInterceptor
    ): OkHttpClient {
        val prettyLoggingInterceptor = PrettyHttpLoggingInterceptor()
        return OkHttpClient.Builder()
            .addInterceptor(superToneApiInterceptor)
            .addInterceptor(prettyLoggingInterceptor)
            .build()
    }

    // SuperTone API 서비스 제공
    @Provides
    @Singleton
    @Named("supertone")
    fun provideSuperToneRetrofit(
        @Named("SuperToneOkHttpClient") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://supertoneapi.com/") // SuperTone API 기본 URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideSuperToneApiService(
        @Named("supertone") retrofit: Retrofit
    ): SuperToneApiService {
        return retrofit.create(SuperToneApiService::class.java)
    }
}
