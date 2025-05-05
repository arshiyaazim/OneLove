package com.kilagee.onelove

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OneLoveApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide configurations here
    }
}