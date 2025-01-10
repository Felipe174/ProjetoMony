
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
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.storage)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.identity.jvm)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

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

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.compose.foundation:foundation-layout:1.7.6")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")

    // Hilt for dependency injection
    implementation("com.google.dagger:hilt-android:2.54")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Lifecycle (for StateFlow, ViewModel, LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // Gson (se necessário para parsing de objetos)
    implementation("com.google.code.gson:gson:2.11.0")

    // Material Icons
    implementation("androidx.compose.material:material-icons-core:1.7.6")
    implementation("androidx.compose.material:material-icons-extended:1.7.6")

    // Unit testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Hilt testing (se necessário)
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.54")

    // Outras dependências necessárias
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.play.services.wallet)
    implementation(libs.firebase.dataconnect)
    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.generativeai)
}

