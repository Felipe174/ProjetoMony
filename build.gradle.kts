
buildscript {
    repositories {
        google()  // Necessário para Compose
        mavenCentral()  // Para outras dependências
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.8.0")
        classpath ("com.google.gms:google-services:4.3.8")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.53") // Plugin Hilt
    }
}


plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

