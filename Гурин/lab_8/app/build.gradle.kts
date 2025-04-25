plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "by.rpomp.lab_8"
    compileSdk = 35

    defaultConfig {
        applicationId = "by.rpomp.lab_8"
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

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

//    implementation("com.google.android.gms:play-services-maps:21.0.0")
//    implementation("com.google.android.gms:play-services-location:21.0.0")


    implementation(libs.okhttp)
    implementation(libs.osmdroid)
    implementation(libs.playservices.location)
}