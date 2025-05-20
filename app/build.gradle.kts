plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.example.mony"
    compileSdk = 35

    buildFeatures {
        compose = true
    }


    defaultConfig {
        applicationId = "com.example.mony"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "2.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    signingConfigs {
        create("release") {
            keyAlias = "MyKey"
            keyPassword = "718293"
            storeFile = file("keystore/my-release-key.jks")
            storePassword = "718293"
    }

        buildTypes {
            getByName("release") {
                signingConfig = signingConfigs.getByName("release")
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
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
    buildToolsVersion = "35.0.0"
    configurations.all {
        resolutionStrategy {
            force("androidx.test.espresso:espresso-core:3.6.1")
        }
    }
}


dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore.ktx)
    // Glide
    implementation(libs.glide)
    implementation(libs.androidx.storage)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.identity.jvm)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.media3.common.ktx)
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
    implementation(libs.androidx.material3)
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
    implementation(libs.gson)

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
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.play.services.wallet)
    implementation(libs.firebase.dataconnect)
    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.generativeai)
    implementation (libs.accompanist.pager)
    implementation (libs.accompanist.pager.indicators)
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.36.0")



}

