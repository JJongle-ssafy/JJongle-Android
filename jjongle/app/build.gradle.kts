

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.androidx.baselineprofile)
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ssafy.jjongle"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ssafy.jjongle"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"")
        }

        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"http://i13d106.p.ssafy.io:8080/\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
            isProfileable = true
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    // Kapt 설정
    kapt {
        correctErrorTypes = true
    }
    hilt {
        enableAggregatingTask = false
    }

    baselineProfile {
        automaticGenerationDuringBuild = false
    }

    composeCompiler {
        stabilityConfigurationFile.set(rootProject.layout.projectDirectory.file("compose_stability.conf"))
    }
}

dependencies {
    implementation(project(":common:data"))
    implementation(project(":common:domain"))
    implementation(project(":common:presentation"))
    implementation(project(":main:entity"))
    implementation(project(":main:data"))
    implementation(project(":main:presentation"))
    implementation(project(":oxgame:entity"))
    implementation(project(":oxgame:domain"))
    implementation(project(":oxgame:data"))
    implementation(project(":oxgame:presentation"))
    implementation(project(":tangram:entity"))
    implementation(project(":tangram:domain"))
    implementation(project(":tangram:data"))
    implementation(project(":tangram:presentation"))

    // 기존 Compose 의존성
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // 클린 아키텍처 의존성
    // Hilt (의존성 주입)
    implementation(libs.hilt.android)
    implementation(libs.androidx.lifecycle.process)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coroutines (비동기 처리)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.navigation3.runtime)

    // ViewModel
    implementation(libs.viewmodel.compose)

    // 애니메이션 의존성
    implementation(libs.androidx.compose.animation)


    // 테스트 의존성
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
    baselineProfile(project(":baselineprofile"))
}
