package com.example.mony;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa o Firebase
            FirebaseApp.initializeApp(this)
        FirebaseFirestore.getInstance().enableNetwork() // Habilita a rede do Firestore

    }
}

