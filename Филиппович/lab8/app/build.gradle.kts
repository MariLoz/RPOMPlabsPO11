plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.lab8"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lab8"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4'")
    implementation ("org.osmdroid:osmdroid-android:6.1.16") // Библиотека osmdroid для карт
    implementation ("com.google.android.gms:play-services-location:21.0.1") // Для работы с геолокацией
    implementation ("androidx.preference:preference:1.2.0") // Для работы с SharedPreferences

}