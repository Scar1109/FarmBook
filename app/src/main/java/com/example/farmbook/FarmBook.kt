package com.example.farmbook

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class FarmBook : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d("com.example.farmbook.FarmBook", "FirebaseApp initialized in com.example.farmbook.FarmBook class")
    }
}
