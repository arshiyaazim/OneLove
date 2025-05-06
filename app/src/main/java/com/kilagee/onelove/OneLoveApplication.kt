package com.kilagee.onelove

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class OneLoveApplication : Application() {

    companion object {
        const val CHAT_NOTIFICATION_CHANNEL_ID = "chat_notification_channel"
        const val MATCHES_NOTIFICATION_CHANNEL_ID = "matches_notification_channel"
        const val OFFERS_NOTIFICATION_CHANNEL_ID = "offers_notification_channel"
        const val PAYMENTS_NOTIFICATION_CHANNEL_ID = "payments_notification_channel"
        const val GENERAL_NOTIFICATION_CHANNEL_ID = "general_notification_channel"
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize ThreeTenABP for better date handling
        AndroidThreeTen.init(this)
        
        // Set up Notification Channels for Android O and above
        createNotificationChannels()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Chat Notifications Channel
            val chatChannel = NotificationChannel(
                CHAT_NOTIFICATION_CHANNEL_ID,
                "Chat Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new chat messages"
                enableLights(true)
                enableVibration(true)
            }
            
            // Matches Notifications Channel
            val matchesChannel = NotificationChannel(
                MATCHES_NOTIFICATION_CHANNEL_ID,
                "Match Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new matches"
                enableLights(true)
                enableVibration(true)
            }
            
            // Offers Notifications Channel
            val offersChannel = NotificationChannel(
                OFFERS_NOTIFICATION_CHANNEL_ID,
                "Offer Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for offers and requests"
                enableLights(true)
                enableVibration(true)
            }
            
            // Payments Notifications Channel
            val paymentsChannel = NotificationChannel(
                PAYMENTS_NOTIFICATION_CHANNEL_ID,
                "Payment Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for payments and subscriptions"
                enableLights(true)
                enableVibration(true)
            }
            
            // General Notifications Channel
            val generalChannel = NotificationChannel(
                GENERAL_NOTIFICATION_CHANNEL_ID,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
                enableLights(true)
                enableVibration(true)
            }
            
            notificationManager.createNotificationChannels(
                listOf(
                    chatChannel,
                    matchesChannel,
                    offersChannel,
                    paymentsChannel,
                    generalChannel
                )
            )
        }
    }
}