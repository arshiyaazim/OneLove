package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Represents a notification for a user
 */
@Parcelize
data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val status: NotificationStatus = NotificationStatus.UNREAD,
    val timestamp: Date = Date(),
    val readAt: Date? = null,
    val data: Map<String, String> = mapOf(), // Additional data for navigation
    val relatedUserId: String = "", // ID of user related to this notification
    val relatedItemId: String = "", // ID of item (match, message, etc.) related to this notification
    val imageUrl: String = "", // URL to notification image
    val priority: NotificationPriority = NotificationPriority.DEFAULT
) : Parcelable

enum class NotificationType {
    MATCH, // New match
    MESSAGE, // New message
    CALL, // Missed/incoming call
    OFFER, // New offer
    SUBSCRIPTION, // Subscription related (expiry, renewal)
    PAYMENT, // Payment related
    VERIFICATION, // Verification update
    POINTS, // Points related
    PROFILE_VIEW, // Someone viewed your profile
    SYSTEM // System notification
}

enum class NotificationStatus {
    UNREAD, READ, ARCHIVED, DELETED
}

enum class NotificationPriority {
    LOW, DEFAULT, HIGH, URGENT
}