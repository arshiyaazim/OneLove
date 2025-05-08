package com.kilagee.onelove

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Base application class for OneLove app
 * 
 * Initializes core components:
 * - Hilt dependency injection
 * - Firebase
 * - App Check security
 * - Timber logging
 * - WorkManager
 * - Notification channels
 */
@HiltAndroidApp
class OneLoveApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // App Check for security
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Create notification channels for Android O and above
        createNotificationChannels()
    }
    
    /**
     * Configure WorkManager with Hilt integration
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
    
    /**
     * Create notification channels for different priority levels
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // High priority channel (matches, messages, calls)
            val highPriorityChannel = NotificationChannel(
                HIGH_PRIORITY_CHANNEL_ID,
                "High Priority Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important notifications like new matches, messages and calls"
                enableVibration(true)
                enableLights(true)
            }
            
            // Medium priority channel (likes, profile views)
            val mediumPriorityChannel = NotificationChannel(
                MEDIUM_PRIORITY_CHANNEL_ID,
                "Medium Priority Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Medium priority notifications like likes and profile views"
                enableVibration(true)
            }
            
            // Low priority channel (announcements, tips)
            val lowPriorityChannel = NotificationChannel(
                LOW_PRIORITY_CHANNEL_ID,
                "Low Priority Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Low priority notifications like tips and announcements"
            }
            
            notificationManager.createNotificationChannels(
                listOf(highPriorityChannel, mediumPriorityChannel, lowPriorityChannel)
            )
        }
    }
    
    companion object {
        const val HIGH_PRIORITY_CHANNEL_ID = "high_priority"
        const val MEDIUM_PRIORITY_CHANNEL_ID = "medium_priority"
        const val LOW_PRIORITY_CHANNEL_ID = "low_priority"
    }
}