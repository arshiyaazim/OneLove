pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("com.android.application") version "8.2.0"
        id("com.android.library") version "8.2.0"
        id("org.jetbrains.kotlin.android") version "1.9.20"
        id("dagger.hilt.android.plugin") version "2.50"
        id("com.google.gms.google-services") version "4.4.1"
        id("com.google.firebase.crashlytics") version "2.9.9"
        id("com.google.firebase.firebase-perf") version "1.4.2" // ✅ ADD THIS
    }
}
