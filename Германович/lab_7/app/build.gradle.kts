plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "by.rpomp.lab_7"
    compileSdk = 35

    defaultConfig {
        applicationId = "by.rpomp.lab_7"
        minSdk = 29
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
}

val camerax_version = "1.1.0-alpha08"
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // CameraX core library using the camera2 implementation
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")

    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:$camerax_version")

    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:1.0.0-alpha14")

    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:1.0.0-alpha14")
}