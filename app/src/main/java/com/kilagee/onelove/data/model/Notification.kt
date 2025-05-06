package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing notification type
 */
enum class NotificationType {
    MESSAGE, MATCH, OFFER, SUBSCRIPTION, SYSTEM, VERIFICATION, POINTS, ADMIN, CALL
}

/**
 * Enum representing notification priority
 */
enum class NotificationPriority {
    LOW, NORMAL, HIGH
}

/**
 * Notification data class for Firestore mapping
 */
data class Notification(
    @DocumentId
    val id: String = "",
    
    // Basic info
    val title: String = "",
    val body: String = "",
    val imageUrl: String? = null,
    
    // Recipient
    val userId: String = "",
    val additionalRecipients: List<String> = emptyList(),
    
    // Notification details
    val notificationType: NotificationType = NotificationType.SYSTEM,
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val sound: String? = null,
    
    // Related entities
    val senderId: String? = null,
    val senderName: String? = null,
    val senderPhotoUrl: String? = null,
    val relatedEntityId: String? = null,
    val relatedEntityType: String? = null,
    val deepLink: String? = null,
    
    // Status
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val isDeleted: Boolean = false,
    
    // Action buttons
    val actionButtons: List<NotificationAction> = emptyList(),
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    val readAt: Timestamp? = null,
    val deliveredAt: Timestamp? = null,
    val expiresAt: Timestamp? = null,
    
    // Additional data
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if notification has expired
     */
    fun isExpired(): Boolean {
        val expiry = expiresAt?.toDate() ?: return false
        return expiry.before(java.util.Date())
    }
    
    /**
     * Get formatted timestamp
     */
    fun getFormattedTime(): String {
        val date = createdAt?.toDate() ?: return ""
        val now = java.util.Date()
        val diff = now.time - date.time
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hours ago"
            days < 7 -> "$days days ago"
            else -> {
                val formatter = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                formatter.format(date)
            }
        }
    }
    
    /**
     * Get icon resource name based on notification type
     */
    fun getIconResourceName(): String {
        return when (notificationType) {
            NotificationType.MESSAGE -> "ic_notification_message"
            NotificationType.MATCH -> "ic_notification_match"
            NotificationType.OFFER -> "ic_notification_offer"
            NotificationType.SUBSCRIPTION -> "ic_notification_subscription"
            NotificationType.SYSTEM -> "ic_notification_system"
            NotificationType.VERIFICATION -> "ic_notification_verification"
            NotificationType.POINTS -> "ic_notification_points"
            NotificationType.ADMIN -> "ic_notification_admin"
            NotificationType.CALL -> "ic_notification_call"
        }
    }
    
    /**
     * Convert to push notification data map
     */
    fun toPushNotificationData(): Map<String, String> {
        val data = mutableMapOf(
            "title" to title,
            "body" to body,
            "notification_id" to id,
            "notification_type" to notificationType.name,
            "priority" to priority.name,
            "created_at" to (createdAt?.seconds?.toString() ?: "")
        )
        
        imageUrl?.let { data["image_url"] = it }
        deepLink?.let { data["deep_link"] = it }
        senderId?.let { data["sender_id"] = it }
        senderName?.let { data["sender_name"] = it }
        senderPhotoUrl?.let { data["sender_photo_url"] = it }
        relatedEntityId?.let { data["related_entity_id"] = it }
        relatedEntityType?.let { data["related_entity_type"] = it }
        
        return data
    }
}

/**
 * Notification action for interactive notifications
 */
data class NotificationAction(
    val id: String = "",
    val title: String = "",
    val deepLink: String = "",
    val icon: String? = null,
    val metadata: Map<String, Any> = emptyMap()
)