// Arquivo build.gradle (Top-Level)
buildscript {
    repositories {
        google()  // Necessário para Compose
        mavenCentral()  // Para outras dependências
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.8.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.43.2") // Plugin Hilt
    }
}


plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

