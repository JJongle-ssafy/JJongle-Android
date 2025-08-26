

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ssafy.jjongle"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ssafy.jjongle"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    // Kapt 설정
    kapt {
        correctErrorTypes = true
    }
    hilt {
        enableAggregatingTask = false
    }
}

dependencies {
    // 기존 Compose 의존성
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // 클린 아키텍처 의존성
    // Hilt (의존성 주입)
    implementation(libs.hilt.android)
    implementation(libs.play.services.games.v2)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.lifecycle.process)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.fragment)
    implementation(libs.hilt.navigation.compose)

    // Room (로컬 데이터베이스)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Retrofit (네트워크 통신)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Coroutines (비동기 처리)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    // ViewModel & Navigation
    implementation(libs.viewmodel.compose)
    implementation(libs.navigation.compose)

    // Coil (이미지 로딩)
    implementation(libs.coil.compose)

    // 애니메이션 의존성
    implementation(libs.androidx.compose.animation)

    // CameraX (카메라)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Google ML Kit Pose Detection
    implementation("com.google.mlkit:pose-detection:18.0.0-beta5")


    // 테스트 의존성
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // lottie-compose (애니메이션)
    implementation("com.airbnb.android:lottie-compose:6.6.7") // 최신 버전 확인 필요
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))


    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    // for background music
    // 안정 버전 사용 (2025-07 기준 stable 1.7.1)
    implementation("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-session:1.7.1")
    implementation("androidx.media3:media3-datasource:1.7.1") // RawResourceDataSource 용

    implementation(project(":MainApp.androidlib"))
    implementation(project(":unityLibrary"))
}