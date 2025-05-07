package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

/**
 * Notification data model
 * Represents a notification that can be sent to a user
 */
@Parcelize
data class Notification(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = TYPE_GENERAL,
    val isRead: Boolean = false,
    val timestamp: Timestamp? = null,
    val readAt: Timestamp? = null,
    val imageUrl: String? = null,
    val deepLink: String? = null,
    val referenceId: String? = null, // ID of related entity (match, message, offer, etc.)
    val referenceType: String? = null, // Type of related entity (match, message, offer, etc.)
    val actionText: String? = null,
    val actionType: String? = null,
    val additionalData: Map<String, Any>? = null,
    val isDeleted: Boolean = false
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val TYPE_GENERAL = "GENERAL"
        const val TYPE_MATCH = "MATCH"
        const val TYPE_MESSAGE = "MESSAGE"
        const val TYPE_OFFER = "OFFER"
        const val TYPE_LIKE = "LIKE"
        const val TYPE_SUPER_LIKE = "SUPER_LIKE"
        const val TYPE_PAYMENT = "PAYMENT"
        const val TYPE_SUBSCRIPTION = "SUBSCRIPTION"
        const val TYPE_VERIFICATION = "VERIFICATION"
        const val TYPE_SYSTEM = "SYSTEM"
        
        const val ACTION_OPEN = "OPEN"
        const val ACTION_REPLY = "REPLY"
        const val ACTION_ACCEPT = "ACCEPT"
        const val ACTION_DECLINE = "DECLINE"
        const val ACTION_VIEW = "VIEW"
    }
}

/**
 * NotificationSettings data model
 * Represents a user's notification preferences
 */
@Parcelize
data class NotificationSettings(
    @DocumentId val id: String = "",
    val userId: String = "",
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val matchNotifications: Boolean = true,
    val messageNotifications: Boolean = true,
    val likeNotifications: Boolean = true,
    val offerNotifications: Boolean = true,
    val paymentNotifications: Boolean = true,
    val systemNotifications: Boolean = true,
    val marketingNotifications: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 22, // 24-hour format (10 PM)
    val quietHoursEnd: Int = 8, // 24-hour format (8 AM)
    val updatedAt: Timestamp? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
}