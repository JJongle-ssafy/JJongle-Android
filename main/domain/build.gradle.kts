plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":main:entity"))
    api(project(":common:domain"))

    testImplementation(libs.junit)
}
