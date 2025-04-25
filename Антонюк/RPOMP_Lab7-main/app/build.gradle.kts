plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.lab7"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lab7"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("androidx.camera:camera-core:1.4.0-alpha01")
    implementation ("androidx.camera:camera-camera2:1.4.0-alpha01")
    implementation ("androidx.camera:camera-lifecycle:1.4.0-alpha01")
    implementation ("androidx.camera:camera-view:1.4.0-alpha01")
    implementation ("androidx.camera:camera-extensions:1.4.0-alpha01")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.exifinterface:exifinterface:1.3.3")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}