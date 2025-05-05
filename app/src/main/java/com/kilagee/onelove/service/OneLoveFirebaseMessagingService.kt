package com.kilagee.onelove.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kilagee.onelove.MainActivity
import com.kilagee.onelove.R
import com.kilagee.onelove.data.model.NotificationActionType
import com.kilagee.onelove.data.model.NotificationType
import com.kilagee.onelove.domain.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Firebase Cloud Messaging service for handling push notifications
 */
@AndroidEntryPoint
class OneLoveFirebaseMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var notificationRepository: NotificationRepository
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Handle FCM messages here
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "OneLove"
            val body = notification.body ?: ""
            
            // Get additional data
            val data = remoteMessage.data
            val notificationType = data["type"]?.let {
                try {
                    NotificationType.valueOf(it)
                } catch (e: Exception) {
                    NotificationType.SYSTEM
                }
            } ?: NotificationType.SYSTEM
            
            val actionType = data["actionType"]?.let {
                try {
                    NotificationActionType.valueOf(it)
                } catch (e: Exception) {
                    NotificationActionType.NONE
                }
            } ?: NotificationActionType.NONE
            
            val notificationId = data["notificationId"]
            val actionData = data["actionData"]
            val relatedId = data["relatedId"]
            val imageUrl = data["imageUrl"]
            
            // Show notification
            sendNotification(
                title,
                body,
                notificationType,
                actionType,
                notificationId,
                actionData,
                relatedId,
                imageUrl
            )
            
            // Store notification in the local database if it's not a transient notification
            if (notificationId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    notificationRepository.createNotification(
                        title = title,
                        message = body,
                        type = notificationType,
                        relatedId = relatedId,
                        imageUrl = imageUrl,
                        actionType = actionType,
                        actionData = actionData
                    )
                }
            }
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // Update token in the repository
        CoroutineScope(Dispatchers.IO).launch {
            notificationRepository.updateFcmToken(token)
        }
    }
    
    /**
     * Create and show a notification
     */
    private fun sendNotification(
        title: String,
        message: String,
        type: NotificationType,
        actionType: NotificationActionType,
        notificationId: String?,
        actionData: String?,
        relatedId: String?,
        imageUrl: String?
    ) {
        // Create intent based on action type
        val intent = createIntentForActionType(actionType, actionData)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Notification sound
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        // Create notification
        val notificationBuilder = NotificationCompat.Builder(this, getChannelId(type))
            .setSmallIcon(R.drawable.ic_notification) // TODO: Replace with actual icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        
        // For call notifications, add answer/decline actions
        if (type == NotificationType.CALL_MISSED && actionData != null) {
            val callId = actionData
            
            // Answer call action
            val answerIntent = Intent(this, MainActivity::class.java).apply {
                action = "ACTION_ANSWER_CALL"
                putExtra("callId", callId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val answerPendingIntent = PendingIntent.getActivity(
                this,
                1,
                answerIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            notificationBuilder.addAction(
                R.drawable.ic_call_answer, // TODO: Replace with actual icon
                "Answer",
                answerPendingIntent
            )
            
            // Decline call action
            val declineIntent = Intent(this, MainActivity::class.java).apply {
                action = "ACTION_DECLINE_CALL"
                putExtra("callId", callId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val declinePendingIntent = PendingIntent.getActivity(
                this,
                2,
                declineIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            notificationBuilder.addAction(
                R.drawable.ic_call_decline, // TODO: Replace with actual icon
                "Decline",
                declinePendingIntent
            )
            
            // Full screen intent for calls
            notificationBuilder.setFullScreenIntent(pendingIntent, true)
        }
        
        // Get notification manager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager, type)
        }
        
        // Show notification
        val notificationIdInt = notificationId?.hashCode()?.absoluteValue ?: 0
        notificationManager.notify(notificationIdInt, notificationBuilder.build())
    }
    
    /**
     * Create intent based on action type
     */
    private fun createIntentForActionType(
        actionType: NotificationActionType,
        actionData: String?
    ): Intent {
        val intent = Intent(this, MainActivity::class.java)
        
        when (actionType) {
            NotificationActionType.OPEN_PROFILE -> {
                intent.action = "ACTION_OPEN_PROFILE"
                intent.putExtra("userId", actionData)
            }
            NotificationActionType.OPEN_CHAT -> {
                intent.action = "ACTION_OPEN_CHAT"
                intent.putExtra("chatId", actionData)
            }
            NotificationActionType.OPEN_OFFER -> {
                intent.action = "ACTION_OPEN_OFFER"
                intent.putExtra("offerId", actionData)
            }
            NotificationActionType.OPEN_PAYMENT -> {
                intent.action = "ACTION_OPEN_PAYMENT"
                intent.putExtra("paymentId", actionData)
            }
            NotificationActionType.OPEN_CALL -> {
                intent.action = "ACTION_OPEN_CALL"
                intent.putExtra("callId", actionData)
            }
            NotificationActionType.OPEN_MATCHES -> {
                intent.action = "ACTION_OPEN_MATCHES"
            }
            NotificationActionType.OPEN_SETTINGS -> {
                intent.action = "ACTION_OPEN_SETTINGS"
            }
            NotificationActionType.NONE -> {
                // No specific action
            }
        }
        
        // Add flags to start a new task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        
        return intent
    }
    
    /**
     * Get channel ID based on notification type
     */
    private fun getChannelId(type: NotificationType): String {
        return when (type) {
            NotificationType.CALL_MISSED, NotificationType.CALL_ENDED -> CHANNEL_ID_CALLS
            NotificationType.MESSAGE -> CHANNEL_ID_MESSAGES
            NotificationType.MATCH -> CHANNEL_ID_MATCHES
            NotificationType.OFFER_RECEIVED, NotificationType.OFFER_ACCEPTED,
            NotificationType.OFFER_REJECTED, NotificationType.OFFER_EXPIRED -> CHANNEL_ID_OFFERS
            NotificationType.PAYMENT_RECEIVED, NotificationType.PAYMENT_SENT -> CHANNEL_ID_PAYMENTS
            NotificationType.PROFILE_VIEW, NotificationType.SYSTEM -> CHANNEL_ID_GENERAL
        }
    }
    
    /**
     * Create notification channel for Android Oreo and above
     */
    private fun createNotificationChannel(notificationManager: NotificationManager, type: NotificationType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (type) {
                NotificationType.CALL_MISSED, NotificationType.CALL_ENDED -> {
                    val channel = NotificationChannel(
                        CHANNEL_ID_CALLS,
                        "Calls",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Call notifications"
                        enableVibration(true)
                        enableLights(true)
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                NotificationType.MESSAGE -> {
                    val channel = NotificationChannel(
                        CHANNEL_ID_MESSAGES,
                        "Messages",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Message notifications"
                        enableVibration(true)
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                NotificationType.MATCH -> {
                    val channel = NotificationChannel(
                        CHANNEL_ID_MATCHES,
                        "Matches",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = "Match notifications"
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                NotificationType.OFFER_RECEIVED, NotificationType.OFFER_ACCEPTED,
                NotificationType.OFFER_REJECTED, NotificationType.OFFER_EXPIRED -> {
                    val channel = NotificationChannel(
                        CHANNEL_ID_OFFERS,
                        "Offers",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = "Offer notifications"
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                NotificationType.PAYMENT_RECEIVED, NotificationType.PAYMENT_SENT -> {
                    val channel = NotificationChannel(
                        CHANNEL_ID_PAYMENTS,
                        "Payments",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = "Payment notifications"
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                NotificationType.PROFILE_VIEW, NotificationType.SYSTEM -> {
                    val channel = NotificationChannel(
                        CHANNEL_ID_GENERAL,
                        "General",
                        NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        description = "General notifications"
                    }
                    notificationManager.createNotificationChannel(channel)
                }
            }
        }
    }
    
    companion object {
        private const val CHANNEL_ID_CALLS = "channel_calls"
        private const val CHANNEL_ID_MESSAGES = "channel_messages"
        private const val CHANNEL_ID_MATCHES = "channel_matches"
        private const val CHANNEL_ID_OFFERS = "channel_offers"
        private const val CHANNEL_ID_PAYMENTS = "channel_payments"
        private const val CHANNEL_ID_GENERAL = "channel_general"
    }
}