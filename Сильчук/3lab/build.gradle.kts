buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0") // Используйте актуальную версию Android Gradle Plugin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // Используйте актуальную версию Kotlin
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}