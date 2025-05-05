package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kilagee.onelove.data.database.converter.DateConverter
import com.kilagee.onelove.data.database.converter.NotificationActionTypeConverter
import com.kilagee.onelove.data.database.converter.NotificationTypeConverter
import java.util.Date

/**
 * Enum for notification types
 */
enum class NotificationType {
    MATCH,              // New match notification
    MESSAGE,            // New message notification
    OFFER_RECEIVED,     // New offer received
    OFFER_ACCEPTED,     // Offer was accepted
    OFFER_REJECTED,     // Offer was rejected
    OFFER_EXPIRED,      // Offer has expired
    PAYMENT_RECEIVED,   // Payment received
    PAYMENT_SENT,       // Payment sent
    CALL_MISSED,        // Missed call
    CALL_ENDED,         // Call ended
    PROFILE_VIEW,       // Someone viewed your profile
    SYSTEM              // System notification
}

/**
 * Enum for notification action types
 */
enum class NotificationActionType {
    OPEN_PROFILE,       // Open a user profile
    OPEN_CHAT,          // Open a chat conversation
    OPEN_OFFER,         // Open an offer
    OPEN_PAYMENT,       // Open payment details
    OPEN_CALL,          // Open call details or initiate a call
    OPEN_MATCHES,       // Open matches screen
    OPEN_SETTINGS,      // Open settings
    NONE                // No action
}

/**
 * Entity class for notifications
 */
@Entity(tableName = "notifications")
@TypeConverters(DateConverter::class, NotificationTypeConverter::class, NotificationActionTypeConverter::class)
data class Notification(
    @PrimaryKey
    val id: String,
    
    // Recipient user ID
    val userId: String,
    
    // Notification title
    val title: String,
    
    // Notification message
    val message: String,
    
    // Notification type
    val type: NotificationType,
    
    // Optional image URL
    val imageUrl: String?,
    
    // ID of related entity (e.g., chat ID, offer ID, etc.)
    val relatedId: String?,
    
    // Type of action when notification is tapped
    val actionType: NotificationActionType,
    
    // Optional data for the action
    val actionData: String?,
    
    // Whether notification has been read
    val isRead: Boolean = false,
    
    // Creation timestamp
    val createdAt: Date,
    
    // Read timestamp
    val readAt: Date? = null
)