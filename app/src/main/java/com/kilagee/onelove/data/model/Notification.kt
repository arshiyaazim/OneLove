package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kilagee.onelove.data.database.converters.DateConverter
import java.util.Date

/**
 * Entity class for notifications
 */
@Entity(tableName = "notifications")
@TypeConverters(DateConverter::class)
data class Notification(
    @PrimaryKey
    val id: String,
    
    val userId: String,
    
    val title: String,
    
    val body: String,
    
    val timestamp: Date,
    
    val read: Boolean = false,
    
    val type: NotificationType,
    
    val data: Map<String, String> = emptyMap(),
    
    val actionType: NotificationActionType? = null,
    
    val actionData: String? = null
)

/**
 * Enumeration of notification types
 */
enum class NotificationType {
    MESSAGE,
    MATCH,
    CALL,
    PAYMENT,
    SUBSCRIPTION,
    OFFER,
    SYSTEM
}

/**
 * Enumeration of notification action types
 */
enum class NotificationActionType {
    OPEN_CONVERSATION,
    OPEN_PROFILE,
    OPEN_CALL,
    OPEN_PAYMENT,
    OPEN_SUBSCRIPTION,
    OPEN_OFFER,
    CUSTOM_ACTIVITY
}