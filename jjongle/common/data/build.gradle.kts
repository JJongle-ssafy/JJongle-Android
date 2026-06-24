plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "com.ssafy.jjongle.common.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"")
        }

        release {
            buildConfigField("String", "API_BASE_URL", "\"http://i13d106.p.ssafy.io:8080/\"")
        }
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":common:domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    api(libs.okhttp)
    api(libs.retrofit)
    api(libs.retrofit.converter.kotlinx.serialization)
    api(libs.kotlinx.serialization.json)
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    api(libs.firebase.auth)
    api(libs.firebase.firestore)
    api(libs.play.services.auth)
    implementation(libs.coroutines.android)
    api(libs.androidx.media3.common.ktx)
    api("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-datasource:1.7.1")

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
