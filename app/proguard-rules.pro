# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Firestore
-keep class com.google.firebase.firestore.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Coil (imagens)
-keep class coil.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**

# ViewModel e LiveData
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Geração automática (caso use)
-keep class com.example.mony.** { *; }
