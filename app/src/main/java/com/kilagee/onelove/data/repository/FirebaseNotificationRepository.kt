package com.kilagee.onelove.data.repository

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.kilagee.onelove.data.database.dao.NotificationDao
import com.kilagee.onelove.data.model.Notification
import com.kilagee.onelove.data.model.NotificationActionType
import com.kilagee.onelove.data.model.NotificationType
import com.kilagee.onelove.domain.model.Resource
import com.kilagee.onelove.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNotificationRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging,
    private val notificationDao: NotificationDao,
    private val sharedPreferences: SharedPreferences
) : NotificationRepository {
    
    private val notificationsCollection = firestore.collection("notifications")
    private val userTokensCollection = firestore.collection("user_tokens")
    private val notificationsEnabledKey = "notifications_enabled"
    
    override fun createNotification(
        title: String,
        message: String,
        type: NotificationType,
        relatedId: String?,
        imageUrl: String?,
        actionType: NotificationActionType,
        actionData: String?
    ): Flow<Resource<Notification>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Create notification object
            val notificationId = UUID.randomUUID().toString()
            val notification = Notification(
                id = notificationId,
                userId = currentUser.uid,
                title = title,
                message = message,
                type = type,
                imageUrl = imageUrl,
                relatedId = relatedId,
                actionType = actionType,
                actionData = actionData,
                createdAt = Date()
            )
            
            // Save to Firestore
            notificationsCollection.document(notificationId).set(notification).await()
            
            // Save to Room
            notificationDao.insertNotification(notification)
            
            // Use Firebase Cloud Messaging to send push notification
            val recipientTokenSnapshot = userTokensCollection.document(currentUser.uid).get().await()
            val recipientToken = recipientTokenSnapshot.getString("token")
            
            if (recipientToken != null) {
                // Send FCM message using Firebase Cloud Functions
                // This will be handled by a Cloud Function that sends the push notification
                val messageData = hashMapOf(
                    "token" to recipientToken,
                    "title" to title,
                    "body" to message,
                    "notificationId" to notificationId,
                    "type" to type.name,
                    "actionType" to actionType.name,
                    "actionData" to (actionData ?: ""),
                    "imageUrl" to (imageUrl ?: "")
                )
                
                // Store message data in a collection that triggers Cloud Functions
                firestore.collection("fcm_messages").document(UUID.randomUUID().toString())
                    .set(messageData).await()
            }
            
            emit(Resource.success(notification))
        } catch (e: Exception) {
            emit(Resource.error("Failed to create notification: ${e.message}"))
        }
    }
    
    override fun getNotifications(): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Get notifications from Firestore
            val notificationsSnapshot = notificationsCollection
                .whereEqualTo("userId", currentUser.uid)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val notifications = notificationsSnapshot.documents.mapNotNull {
                it.toObject(Notification::class.java)
            }
            
            // Save to Room
            notificationDao.insertNotifications(notifications)
            
            // Return notifications
            emit(Resource.success(notifications))
        } catch (e: Exception) {
            // Try to get from local database if network fails
            try {
                val localNotifications = notificationDao.getAllNotifications()
                
                if (localNotifications.isNotEmpty()) {
                    emit(Resource.success(localNotifications))
                } else {
                    emit(Resource.error("Failed to get notifications: ${e.message}"))
                }
            } catch (ex: Exception) {
                emit(Resource.error("Failed to get notifications: ${e.message}"))
            }
        }
    }
    
    override fun getNotificationsByType(type: NotificationType): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Get notifications from Firestore
            val notificationsSnapshot = notificationsCollection
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("type", type.name)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val notifications = notificationsSnapshot.documents.mapNotNull {
                it.toObject(Notification::class.java)
            }
            
            emit(Resource.success(notifications))
        } catch (e: Exception) {
            // Try to get from local database if network fails
            try {
                val localNotifications = notificationDao.getNotificationsByType(type)
                
                if (localNotifications.isNotEmpty()) {
                    emit(Resource.success(localNotifications))
                } else {
                    emit(Resource.error("Failed to get notifications by type: ${e.message}"))
                }
            } catch (ex: Exception) {
                emit(Resource.error("Failed to get notifications by type: ${e.message}"))
            }
        }
    }
    
    override fun getUnreadNotificationsCount(): Flow<Resource<Int>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Get count from Firestore
            val count = notificationsCollection
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("isRead", false)
                .get()
                .await()
                .size()
            
            emit(Resource.success(count))
        } catch (e: Exception) {
            // Try to get from local database if network fails
            try {
                val localCount = notificationDao.getUnreadNotificationsCount()
                emit(Resource.success(localCount))
            } catch (ex: Exception) {
                emit(Resource.error("Failed to get unread notifications count: ${e.message}"))
            }
        }
    }
    
    override fun markNotificationAsRead(notificationId: String): Flow<Resource<Notification>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Update notification in Firestore
            val now = Date()
            notificationsCollection.document(notificationId)
                .update(
                    mapOf(
                        "isRead" to true,
                        "readAt" to now
                    )
                )
                .await()
            
            // Update notification in Room
            notificationDao.markNotificationAsRead(notificationId, now)
            
            // Get updated notification
            val updatedNotification = notificationsCollection.document(notificationId)
                .get()
                .await()
                .toObject(Notification::class.java)
            
            if (updatedNotification != null) {
                emit(Resource.success(updatedNotification))
            } else {
                emit(Resource.error("Notification not found"))
            }
        } catch (e: Exception) {
            emit(Resource.error("Failed to mark notification as read: ${e.message}"))
        }
    }
    
    override fun markAllNotificationsAsRead(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Get all unread notifications
            val unreadNotificationsSnapshot = notificationsCollection
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            
            // Update each notification
            val now = Date()
            val batch = firestore.batch()
            
            unreadNotificationsSnapshot.documents.forEach { doc ->
                batch.update(
                    doc.reference,
                    mapOf(
                        "isRead" to true,
                        "readAt" to now
                    )
                )
            }
            
            batch.commit().await()
            
            // Update notifications in Room
            notificationDao.markAllNotificationsAsRead(now)
            
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error("Failed to mark all notifications as read: ${e.message}"))
        }
    }
    
    override fun deleteNotification(notificationId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Delete notification from Firestore
            notificationsCollection.document(notificationId).delete().await()
            
            // Delete notification from Room
            notificationDao.deleteNotification(notificationId)
            
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error("Failed to delete notification: ${e.message}"))
        }
    }
    
    override fun deleteAllNotifications(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Get all notifications for the user
            val notificationsSnapshot = notificationsCollection
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()
            
            // Delete each notification
            val batch = firestore.batch()
            
            notificationsSnapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            
            batch.commit().await()
            
            // Delete all notifications from Room
            notificationDao.deleteAllNotifications()
            
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error("Failed to delete all notifications: ${e.message}"))
        }
    }
    
    override fun deleteNotificationsByType(type: NotificationType): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Get all notifications of the specified type
            val notificationsSnapshot = notificationsCollection
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("type", type.name)
                .get()
                .await()
            
            // Delete each notification
            val batch = firestore.batch()
            
            notificationsSnapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            
            batch.commit().await()
            
            // Delete notifications from Room
            notificationDao.deleteNotificationsByType(type)
            
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error("Failed to delete notifications by type: ${e.message}"))
        }
    }
    
    override fun updateFcmToken(token: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        
        try {
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Update token in Firestore
            userTokensCollection.document(currentUser.uid)
                .set(
                    mapOf(
                        "token" to token,
                        "userId" to currentUser.uid,
                        "updatedAt" to Date()
                    )
                )
                .await()
            
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error("Failed to update FCM token: ${e.message}"))
        }
    }
    
    override fun setNotificationsEnabled(enabled: Boolean): Flow<Resource<Unit>> = flow {
        try {
            // Store notification preference in SharedPreferences
            sharedPreferences.edit()
                .putBoolean(notificationsEnabledKey, enabled)
                .apply()
            
            // Update Firebase Messaging subscription
            if (enabled) {
                messaging.subscribeToTopic("user_${auth.currentUser?.uid}").await()
            } else {
                messaging.unsubscribeFromTopic("user_${auth.currentUser?.uid}").await()
            }
            
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error("Failed to update notification settings: ${e.message}"))
        }
    }
    
    override fun areNotificationsEnabled(): Flow<Resource<Boolean>> = flow {
        try {
            // Get notification preference from SharedPreferences
            val enabled = sharedPreferences.getBoolean(notificationsEnabledKey, true)
            emit(Resource.success(enabled))
        } catch (e: Exception) {
            emit(Resource.error("Failed to get notification settings: ${e.message}"))
        }
    }
}