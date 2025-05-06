package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing offer type
 */
enum class OfferType {
    DATE, ACTIVITY, MEETUP, VIRTUAL, GIFT, CALL, CUSTOM
}

/**
 * Enum representing offer status
 */
enum class OfferStatus {
    PENDING, ACCEPTED, REJECTED, EXPIRED, CANCELLED, COMPLETED
}

/**
 * Offer data class for Firestore mapping
 */
data class Offer(
    @DocumentId
    val id: String = "",
    
    // Basic info
    val title: String = "",
    val description: String = "",
    
    // Participants
    val senderId: String = "",
    val receiverId: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val senderPhotoUrl: String? = null,
    val receiverPhotoUrl: String? = null,
    
    // Offer details
    val offerType: OfferType = OfferType.CUSTOM,
    val status: OfferStatus = OfferStatus.PENDING,
    val pointsRequired: Int = 0,
    val pointsAwarded: Int = 0,
    val mediaUrls: List<String> = emptyList(),
    val thumbnailUrl: String? = null,
    
    // Location info
    val location: GeoLocation? = null,
    val locationName: String? = null,
    
    // Date/time info
    val scheduledDate: Timestamp? = null,
    val scheduledEndDate: Timestamp? = null,
    val timeZone: String? = null,
    val duration: Int? = null, // in minutes
    
    // Communication
    val chatId: String? = null,
    val matchId: String? = null,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    
    val expiresAt: Timestamp? = null,
    val respondedAt: Timestamp? = null,
    val completedAt: Timestamp? = null,
    
    // Notification status
    val isNotified: Boolean = false,
    val isReminderSent: Boolean = false,
    
    // Additional data
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if offer has expired
     */
    fun isExpired(): Boolean {
        if (status == OfferStatus.EXPIRED) return true
        
        val expiry = expiresAt?.toDate()
        return expiry != null && expiry.before(java.util.Date())
    }
    
    /**
     * Check if offer is active (pending, accepted, not expired)
     */
    fun isActive(): Boolean {
        return status == OfferStatus.PENDING || status == OfferStatus.ACCEPTED && !isExpired()
    }
    
    /**
     * Format scheduled date
     */
    fun formatScheduledDate(): String {
        val date = scheduledDate?.toDate() ?: return ""
        val formatter = java.text.SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Format scheduled time
     */
    fun formatScheduledTime(): String {
        val date = scheduledDate?.toDate() ?: return ""
        val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Check if offer is premium (requires points)
     */
    fun isPremium(): Boolean {
        return pointsRequired > 0
    }
    
    /**
     * Days until the scheduled date
     */
    fun daysUntilScheduled(): Int? {
        val date = scheduledDate?.toDate() ?: return null
        val now = java.util.Date()
        val diff = date.time - now.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * Get formatted offer status
     */
    fun getFormattedStatus(): String {
        return when (status) {
            OfferStatus.PENDING -> "Pending"
            OfferStatus.ACCEPTED -> "Accepted"
            OfferStatus.REJECTED -> "Declined"
            OfferStatus.EXPIRED -> "Expired"
            OfferStatus.CANCELLED -> "Cancelled"
            OfferStatus.COMPLETED -> "Completed"
        }
    }
    
    /**
     * Get formatted offer type
     */
    fun getFormattedType(): String {
        return when (offerType) {
            OfferType.DATE -> "Date"
            OfferType.ACTIVITY -> "Activity"
            OfferType.MEETUP -> "Meetup"
            OfferType.VIRTUAL -> "Virtual Date"
            OfferType.GIFT -> "Gift"
            OfferType.CALL -> "Call"
            OfferType.CUSTOM -> "Custom Offer"
        }
    }
}