package com.todo.shared

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings

class TodoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Configure Firestore for offline persistence
        val firestore = FirebaseFirestore.getInstance()
        val settings = com.google.firebase.firestore.ktx.firestoreSettings {
            isPersistenceEnabled = true
        }
        firestore.firestoreSettings = settings
    }
}
