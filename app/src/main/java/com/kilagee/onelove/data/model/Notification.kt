package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Notification types for the app
 */
enum class NotificationType {
    NEW_MATCH,
    MESSAGE,
    INCOMING_CALL,
    MISSED_CALL,
    LIKE,
    PROFILE_VIEW,
    SUBSCRIPTION,
    VERIFICATION,
    SYSTEM,
    PROMOTIONAL
}

/**
 * Notification entity that represents a single notification
 * Stored in Room database and used for in-app notifications
 */
@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    /**
     * Type of notification
     */
    val type: NotificationType,
    
    /**
     * User ID of the sender
     */
    val senderId: String,
    
    /**
     * Title of the notification
     */
    val title: String,
    
    /**
     * Body content of the notification
     */
    val body: String,
    
    /**
     * Optional image URL
     */
    val imageUrl: String? = null,
    
    /**
     * Timestamp when the notification was created
     */
    val timestamp: Long = System.currentTimeMillis(),
    
    /**
     * Whether the notification has been read
     */
    val isRead: Boolean = false,
    
    /**
     * Deep link URL for navigation when notification is tapped
     */
    val deepLink: String? = null,
    
    /**
     * Optional metadata as JSON string
     */
    val metadata: String? = null
)