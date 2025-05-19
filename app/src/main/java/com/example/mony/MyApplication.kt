package com.example.mony;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // Habilita a persistência offline
            .build()
        firestore.firestoreSettings = settings
        firestore.enableNetwork()
    }
}
