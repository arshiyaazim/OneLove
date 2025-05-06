package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing offer status
 */
enum class OfferStatus {
    PENDING, ACCEPTED, DECLINED, EXPIRED, CANCELED, COMPLETED
}

/**
 * Enum representing offer types
 */
enum class OfferType {
    DATE, VIRTUAL_DATE, VIDEO_CALL, ACTIVITY, GIFT, MEETUP, OTHER;
    
    fun getDisplayName(): String {
        return when (this) {
            DATE -> "Date"
            VIRTUAL_DATE -> "Virtual Date"
            VIDEO_CALL -> "Video Call"
            ACTIVITY -> "Activity"
            GIFT -> "Gift"
            MEETUP -> "Meetup"
            OTHER -> "Other"
        }
    }
    
    fun getIcon(): String {
        return when (this) {
            DATE -> "🍷"
            VIRTUAL_DATE -> "💻"
            VIDEO_CALL -> "📱"
            ACTIVITY -> "🏆"
            GIFT -> "🎁"
            MEETUP -> "🤝"
            OTHER -> "✨"
        }
    }
}

/**
 * Sealed class representing offer content types
 */
sealed class OfferContent {
    data class DateOffer(
        val location: String,
        val dateTime: Timestamp,
        val activity: String,
        val address: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val estimatedDuration: Int? = null, // in minutes
        val reservationRequired: Boolean = false
    ) : OfferContent()
    
    data class VirtualDateOffer(
        val platform: String,
        val dateTime: Timestamp,
        val activity: String,
        val link: String? = null,
        val estimatedDuration: Int? = null // in minutes
    ) : OfferContent()
    
    data class VideoCallOffer(
        val dateTime: Timestamp,
        val duration: Int, // in minutes
        val description: String? = null
    ) : OfferContent()
    
    data class ActivityOffer(
        val activityName: String,
        val description: String,
        val location: String? = null,
        val dateTime: Timestamp? = null,
        val participantsCount: Int = 2,
        val mediaUrl: String? = null
    ) : OfferContent()
    
    data class GiftOffer(
        val giftName: String,
        val description: String,
        val price: Double? = null,
        val currency: String? = null,
        val mediaUrl: String? = null,
        val deliveryMethod: String? = null
    ) : OfferContent()
    
    data class MeetupOffer(
        val location: String,
        val dateTime: Timestamp,
        val purpose: String,
        val address: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null
    ) : OfferContent()
    
    data class CustomOffer(
        val title: String,
        val description: String,
        val dateTime: Timestamp? = null,
        val location: String? = null,
        val mediaUrl: String? = null
    ) : OfferContent()
}

/**
 * Data class for offer model
 */
data class Offer(
    @DocumentId
    val id: String = "",
    
    // Participants
    val senderId: String = "",
    val receiverId: String = "",
    val chatId: String? = null,
    val matchId: String? = null,
    
    // Offer details
    val title: String = "",
    val description: String = "",
    val offerType: OfferType = OfferType.OTHER,
    val status: OfferStatus = OfferStatus.PENDING,
    
    // Content map for type-safe storage
    @get:PropertyName("content")
    @set:PropertyName("content")
    var contentMap: Map<String, Any> = emptyMap(),
    
    // Points and rewards
    val pointsCost: Int = 0,
    val pointsReward: Int = 0,
    val isPremiumOnly: Boolean = false,
    
    // Media
    val mediaUrl: String? = null,
    val mediaThumbnailUrl: String? = null,
    
    // Status tracking
    val isViewed: Boolean = false,
    val reminderSent: Boolean = false,
    val isExpired: Boolean = false,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    val expiresAt: Timestamp? = null,
    val scheduledFor: Timestamp? = null,
    val respondedAt: Timestamp? = null,
    val viewedAt: Timestamp? = null,
    val completedAt: Timestamp? = null,
    
    // Feedback and rating
    val senderRating: Int? = null,
    val receiverRating: Int? = null,
    val senderFeedback: String? = null,
    val receiverFeedback: String? = null,
    
    // Additional data
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Get typed offer content
     */
    @Exclude
    fun getTypedContent(): OfferContent {
        return when (offerType) {
            OfferType.DATE -> {
                OfferContent.DateOffer(
                    location = contentMap["location"] as? String ?: "",
                    dateTime = contentMap["dateTime"] as? Timestamp ?: Timestamp.now(),
                    activity = contentMap["activity"] as? String ?: "",
                    address = contentMap["address"] as? String,
                    latitude = (contentMap["latitude"] as? Number)?.toDouble(),
                    longitude = (contentMap["longitude"] as? Number)?.toDouble(),
                    estimatedDuration = (contentMap["estimatedDuration"] as? Number)?.toInt(),
                    reservationRequired = contentMap["reservationRequired"] as? Boolean ?: false
                )
            }
            OfferType.VIRTUAL_DATE -> {
                OfferContent.VirtualDateOffer(
                    platform = contentMap["platform"] as? String ?: "",
                    dateTime = contentMap["dateTime"] as? Timestamp ?: Timestamp.now(),
                    activity = contentMap["activity"] as? String ?: "",
                    link = contentMap["link"] as? String,
                    estimatedDuration = (contentMap["estimatedDuration"] as? Number)?.toInt()
                )
            }
            OfferType.VIDEO_CALL -> {
                OfferContent.VideoCallOffer(
                    dateTime = contentMap["dateTime"] as? Timestamp ?: Timestamp.now(),
                    duration = (contentMap["duration"] as? Number)?.toInt() ?: 30,
                    description = contentMap["description"] as? String
                )
            }
            OfferType.ACTIVITY -> {
                OfferContent.ActivityOffer(
                    activityName = contentMap["activityName"] as? String ?: "",
                    description = contentMap["description"] as? String ?: "",
                    location = contentMap["location"] as? String,
                    dateTime = contentMap["dateTime"] as? Timestamp,
                    participantsCount = (contentMap["participantsCount"] as? Number)?.toInt() ?: 2,
                    mediaUrl = contentMap["mediaUrl"] as? String
                )
            }
            OfferType.GIFT -> {
                OfferContent.GiftOffer(
                    giftName = contentMap["giftName"] as? String ?: "",
                    description = contentMap["description"] as? String ?: "",
                    price = (contentMap["price"] as? Number)?.toDouble(),
                    currency = contentMap["currency"] as? String,
                    mediaUrl = contentMap["mediaUrl"] as? String,
                    deliveryMethod = contentMap["deliveryMethod"] as? String
                )
            }
            OfferType.MEETUP -> {
                OfferContent.MeetupOffer(
                    location = contentMap["location"] as? String ?: "",
                    dateTime = contentMap["dateTime"] as? Timestamp ?: Timestamp.now(),
                    purpose = contentMap["purpose"] as? String ?: "",
                    address = contentMap["address"] as? String,
                    latitude = (contentMap["latitude"] as? Number)?.toDouble(),
                    longitude = (contentMap["longitude"] as? Number)?.toDouble()
                )
            }
            OfferType.OTHER -> {
                OfferContent.CustomOffer(
                    title = contentMap["title"] as? String ?: title,
                    description = contentMap["description"] as? String ?: description,
                    dateTime = contentMap["dateTime"] as? Timestamp,
                    location = contentMap["location"] as? String,
                    mediaUrl = contentMap["mediaUrl"] as? String
                )
            }
        }
    }
    
    /**
     * Create content map from typed content
     */
    @Exclude
    fun createContentMap(content: OfferContent): Map<String, Any> {
        return when (content) {
            is OfferContent.DateOffer -> {
                mapOf(
                    "location" to content.location,
                    "dateTime" to content.dateTime,
                    "activity" to content.activity,
                    "address" to (content.address ?: ""),
                    "latitude" to (content.latitude ?: 0.0),
                    "longitude" to (content.longitude ?: 0.0),
                    "estimatedDuration" to (content.estimatedDuration ?: 0),
                    "reservationRequired" to content.reservationRequired
                ).filterValues { it != "" && it != 0.0 && it != 0 }
            }
            is OfferContent.VirtualDateOffer -> {
                mapOf(
                    "platform" to content.platform,
                    "dateTime" to content.dateTime,
                    "activity" to content.activity,
                    "link" to (content.link ?: ""),
                    "estimatedDuration" to (content.estimatedDuration ?: 0)
                ).filterValues { it != "" && it != 0 }
            }
            is OfferContent.VideoCallOffer -> {
                mapOf(
                    "dateTime" to content.dateTime,
                    "duration" to content.duration,
                    "description" to (content.description ?: "")
                ).filterValues { it != "" }
            }
            is OfferContent.ActivityOffer -> {
                val map = mutableMapOf(
                    "activityName" to content.activityName,
                    "description" to content.description,
                    "participantsCount" to content.participantsCount
                )
                
                content.location?.let { map["location"] = it }
                content.dateTime?.let { map["dateTime"] = it }
                content.mediaUrl?.let { map["mediaUrl"] = it }
                
                map
            }
            is OfferContent.GiftOffer -> {
                val map = mutableMapOf(
                    "giftName" to content.giftName,
                    "description" to content.description
                )
                
                content.price?.let { map["price"] = it }
                content.currency?.let { map["currency"] = it }
                content.mediaUrl?.let { map["mediaUrl"] = it }
                content.deliveryMethod?.let { map["deliveryMethod"] = it }
                
                map
            }
            is OfferContent.MeetupOffer -> {
                val map = mutableMapOf(
                    "location" to content.location,
                    "dateTime" to content.dateTime,
                    "purpose" to content.purpose
                )
                
                content.address?.let { map["address"] = it }
                content.latitude?.let { map["latitude"] = it }
                content.longitude?.let { map["longitude"] = it }
                
                map
            }
            is OfferContent.CustomOffer -> {
                val map = mutableMapOf(
                    "title" to content.title,
                    "description" to content.description
                )
                
                content.dateTime?.let { map["dateTime"] = it }
                content.location?.let { map["location"] = it }
                content.mediaUrl?.let { map["mediaUrl"] = it }
                
                map
            }
        }
    }
    
    /**
     * Check if offer is active
     */
    @Exclude
    fun isActive(): Boolean {
        return status == OfferStatus.PENDING && 
                !isExpired && 
                (expiresAt == null || expiresAt.toDate().after(java.util.Date()))
    }
    
    /**
     * Get time until expiration in milliseconds
     */
    @Exclude
    fun getTimeUntilExpiration(): Long {
        if (isExpired || status != OfferStatus.PENDING) return 0
        
        val expires = expiresAt?.toDate()?.time ?: return 0
        val now = System.currentTimeMillis()
        
        return (expires - now).coerceAtLeast(0)
    }
    
    /**
     * Format the expiration time
     */
    @Exclude
    fun getFormattedExpirationTime(): String {
        if (isExpired) return "Expired"
        if (status != OfferStatus.PENDING) return "No longer pending"
        
        val timeLeft = getTimeUntilExpiration()
        
        return when {
            timeLeft == 0L -> "Expired"
            timeLeft < 60 * 1000 -> "Expires in less than a minute"
            timeLeft < 60 * 60 * 1000 -> {
                val minutes = timeLeft / (60 * 1000)
                "Expires in $minutes minute${if (minutes > 1) "s" else ""}"
            }
            timeLeft < 24 * 60 * 60 * 1000 -> {
                val hours = timeLeft / (60 * 60 * 1000)
                "Expires in $hours hour${if (hours > 1) "s" else ""}"
            }
            else -> {
                val days = timeLeft / (24 * 60 * 60 * 1000)
                "Expires in $days day${if (days > 1) "s" else ""}"
            }
        }
    }
    
    /**
     * Get formatted date/time for the offer
     */
    @Exclude
    fun getFormattedDateTime(): String {
        val content = getTypedContent()
        val dateTime = when (content) {
            is OfferContent.DateOffer -> content.dateTime
            is OfferContent.VirtualDateOffer -> content.dateTime
            is OfferContent.VideoCallOffer -> content.dateTime
            is OfferContent.MeetupOffer -> content.dateTime
            is OfferContent.ActivityOffer -> content.dateTime
            is OfferContent.CustomOffer -> content.dateTime
            is OfferContent.GiftOffer -> null
        } ?: return "No date specified"
        
        val formatter = java.text.SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", java.util.Locale.getDefault())
        return formatter.format(dateTime.toDate())
    }
    
    /**
     * Get location text for the offer
     */
    @Exclude
    fun getLocationText(): String {
        val content = getTypedContent()
        return when (content) {
            is OfferContent.DateOffer -> content.location
            is OfferContent.VirtualDateOffer -> content.platform
            is OfferContent.VideoCallOffer -> "Video Call"
            is OfferContent.MeetupOffer -> content.location
            is OfferContent.ActivityOffer -> content.location ?: "No location specified"
            is OfferContent.CustomOffer -> content.location ?: "No location specified"
            is OfferContent.GiftOffer -> "Gift"
        }
    }
    
    /**
     * Get status text
     */
    @Exclude
    fun getStatusText(): String {
        return when (status) {
            OfferStatus.PENDING -> "Pending"
            OfferStatus.ACCEPTED -> "Accepted"
            OfferStatus.DECLINED -> "Declined"
            OfferStatus.EXPIRED -> "Expired"
            OfferStatus.CANCELED -> "Canceled"
            OfferStatus.COMPLETED -> "Completed"
        }
    }
    
    /**
     * Get status color (hex code)
     */
    @Exclude
    fun getStatusColor(): String {
        return when (status) {
            OfferStatus.PENDING -> "#FFC107" // Amber
            OfferStatus.ACCEPTED -> "#4CAF50" // Green
            OfferStatus.DECLINED -> "#F44336" // Red
            OfferStatus.EXPIRED -> "#9E9E9E" // Gray
            OfferStatus.CANCELED -> "#9E9E9E" // Gray
            OfferStatus.COMPLETED -> "#2196F3" // Blue
        }
    }
    
    /**
     * Get a new offer with updated status
     */
    @Exclude
    fun withStatus(newStatus: OfferStatus): Offer {
        return copy(
            status = newStatus,
            respondedAt = if (newStatus != OfferStatus.PENDING) Timestamp.now() else respondedAt
        )
    }
    
    /**
     * Get short description text for notifications
     */
    @Exclude
    fun getNotificationText(): String {
        return when (offerType) {
            OfferType.DATE -> "Date offer: $title"
            OfferType.VIRTUAL_DATE -> "Virtual date offer: $title"
            OfferType.VIDEO_CALL -> "Video call offer"
            OfferType.ACTIVITY -> "Activity offer: $title"
            OfferType.GIFT -> "Gift offer: $title"
            OfferType.MEETUP -> "Meetup offer: $title"
            OfferType.OTHER -> "New offer: $title"
        }
    }
    
    /**
     * Companion object with factory methods for creating offers
     */
    companion object {
        /**
         * Create a date offer
         */
        fun createDateOffer(
            senderId: String,
            receiverId: String,
            matchId: String,
            title: String,
            description: String,
            location: String,
            dateTime: java.util.Date,
            activity: String,
            address: String? = null,
            latitude: Double? = null,
            longitude: Double? = null,
            estimatedDuration: Int? = null,
            reservationRequired: Boolean = false,
            expiresInHours: Int = 24
        ): Offer {
            val content = OfferContent.DateOffer(
                location = location,
                dateTime = Timestamp(dateTime),
                activity = activity,
                address = address,
                latitude = latitude,
                longitude = longitude,
                estimatedDuration = estimatedDuration,
                reservationRequired = reservationRequired
            )
            
            val now = Timestamp.now()
            val expiry = Timestamp(java.util.Date(System.currentTimeMillis() + expiresInHours * 60 * 60 * 1000))
            
            val offer = Offer(
                senderId = senderId,
                receiverId = receiverId,
                matchId = matchId,
                title = title,
                description = description,
                offerType = OfferType.DATE,
                status = OfferStatus.PENDING,
                createdAt = now,
                expiresAt = expiry,
                scheduledFor = Timestamp(dateTime)
            )
            
            return offer.copy(contentMap = offer.createContentMap(content))
        }
        
        /**
         * Create a video call offer
         */
        fun createVideoCallOffer(
            senderId: String,
            receiverId: String,
            matchId: String,
            dateTime: java.util.Date,
            duration: Int,
            description: String? = null,
            expiresInHours: Int = 24
        ): Offer {
            val content = OfferContent.VideoCallOffer(
                dateTime = Timestamp(dateTime),
                duration = duration,
                description = description
            )
            
            val now = Timestamp.now()
            val expiry = Timestamp(java.util.Date(System.currentTimeMillis() + expiresInHours * 60 * 60 * 1000))
            
            val offer = Offer(
                senderId = senderId,
                receiverId = receiverId,
                matchId = matchId,
                title = "Video Call",
                description = description ?: "Video call for $duration minutes",
                offerType = OfferType.VIDEO_CALL,
                status = OfferStatus.PENDING,
                createdAt = now,
                expiresAt = expiry,
                scheduledFor = Timestamp(dateTime)
            )
            
            return offer.copy(contentMap = offer.createContentMap(content))
        }
    }
}