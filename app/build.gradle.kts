
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.example.mony"
    compileSdk = 35 // Versão atual do SDK estável



    buildFeatures {
        viewBinding = false // Desative se estiver usando apenas Compose
        compose = true
    }

    defaultConfig {
        applicationId = "com.example.mony"
        minSdk = 23
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {
    // Firebase
    implementation(libs.firebase.bom)
    implementation(libs.firebase.analytics)
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation(libs.firebase.database)
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Glide
    implementation(libs.glide)
    implementation(libs.androidx.storage)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.identity.jvm)
    annotationProcessor(libs.compiler)

    // Jetpack Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) {
        exclude(group = "androidx.appcompat", module = "appcompat")
    }
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.constraintlayout.compose)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    // Lifecycle (for StateFlow, ViewModel, LiveData)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Gson (se necessário para parsing de objetos)
    implementation("com.google.code.gson:gson:2.11.0")

    // Material Icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Unit testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt testing (se necessário)
    androidTestImplementation(libs.hilt.android.testing)

    // Outras dependências necessárias
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.play.services.wallet)
    implementation(libs.firebase.dataconnect)
    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.generativeai)
}

