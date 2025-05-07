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
import javax.inject.Inject

@HiltAndroidApp
class OneLoveApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Set up Firebase App Check for enhanced security
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        
        // Create notification channels
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Match notifications
            val matchChannel = NotificationChannel(
                CHANNEL_MATCHES,
                getString(R.string.new_matches),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new matches"
                enableVibration(true)
            }
            
            // Message notifications
            val messageChannel = NotificationChannel(
                CHANNEL_MESSAGES,
                getString(R.string.messages),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new messages"
                enableVibration(true)
            }
            
            // Call notifications
            val callChannel = NotificationChannel(
                CHANNEL_CALLS,
                getString(R.string.call),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming calls"
                enableVibration(true)
            }
            
            // Offer notifications
            val offerChannel = NotificationChannel(
                CHANNEL_OFFERS,
                getString(R.string.message_offer),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new offers"
                enableVibration(true)
            }
            
            // System notifications
            val systemChannel = NotificationChannel(
                CHANNEL_SYSTEM,
                getString(R.string.system_notifications),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "System notifications and updates"
            }
            
            notificationManager.createNotificationChannels(
                listOf(matchChannel, messageChannel, callChannel, offerChannel, systemChannel)
            )
        }
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
    
    companion object {
        // Notification channel IDs
        const val CHANNEL_MATCHES = "channel_matches"
        const val CHANNEL_MESSAGES = "channel_messages"
        const val CHANNEL_CALLS = "channel_calls"
        const val CHANNEL_OFFERS = "channel_offers"
        const val CHANNEL_SYSTEM = "channel_system"
    }
}