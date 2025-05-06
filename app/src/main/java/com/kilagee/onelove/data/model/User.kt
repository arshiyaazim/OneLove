package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Enum representing user gender
 */
enum class UserGender {
    MALE, FEMALE, NON_BINARY, OTHER, PREFER_NOT_TO_SAY
}

/**
 * Enum representing user verification status
 */
enum class VerificationStatus {
    NOT_VERIFIED, PENDING, VERIFIED, REJECTED
}

/**
 * Enum representing user subscription tier
 */
enum class SubscriptionTier {
    FREE, BASIC, PREMIUM, VIP
}

/**
 * User data class for Firestore mapping
 */
data class User(
    @DocumentId
    val id: String = "",
    
    // Basic info
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String? = null,
    val bio: String = "",
    val birthDate: Date? = null,
    val gender: UserGender = UserGender.PREFER_NOT_TO_SAY,
    val genderPreference: List<UserGender> = emptyList(),
    val location: GeoLocation? = null,
    val interests: List<String> = emptyList(),
    
    // Profile media
    val profilePhotoUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val photos: List<String> = emptyList(),
    
    // Stats and metrics
    val points: Int = 0,
    val matchesCount: Int = 0,
    val likesCount: Int = 0,
    val offersCount: Int = 0,
    
    // Status
    val isOnline: Boolean = false,
    val isPremium: Boolean = false,
    val isVerified: Boolean = false,
    val isLiked: Boolean = false,
    val isAdmin: Boolean = false,
    val isBanned: Boolean = false,
    val verificationStatus: VerificationStatus = VerificationStatus.NOT_VERIFIED,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val subscriptionExpiryDate: Date? = null,
    val verificationDocuments: List<String> = emptyList(),
    
    // Settings
    val showLocation: Boolean = true,
    val showOnlineStatus: Boolean = true,
    val notificationEnabled: Boolean = true,
    val emailNotificationEnabled: Boolean = true,
    val profileVisibility: Boolean = true,
    val maxDistanceInKm: Int = 100,
    val minAgePreference: Int = 18,
    val maxAgePreference: Int = 99,
    val language: String = "en",
    
    // Relationships
    val blockedUsers: List<String> = emptyList(),
    
    // Device info
    val fcmTokens: List<String> = emptyList(),
    val lastKnownDevice: String? = null,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val lastActive: Timestamp? = null,
    
    // Extra fields
    @get:PropertyName("extraData")
    @set:PropertyName("extraData")
    var extraData: Map<String, Any> = emptyMap()
) {
    /**
     * Calculate user age from birth date
     */
    fun getAge(): Int? {
        return birthDate?.let {
            val today = Date()
            var age = today.year - it.year
            if (today.month < it.month || (today.month == it.month && today.date < it.date)) {
                age--
            }
            return age
        }
    }
    
    /**
     * Check if user subscription is active
     */
    fun hasActiveSubscription(): Boolean {
        return isPremium && subscriptionTier != SubscriptionTier.FREE && 
                (subscriptionExpiryDate == null || subscriptionExpiryDate.after(Date()))
    }
    
    /**
     * Days remaining in subscription
     */
    fun getSubscriptionDaysRemaining(): Int {
        return subscriptionExpiryDate?.let {
            val today = Date()
            val diff = it.time - today.time
            return (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        } ?: 0
    }
    
    /**
     * Check if user has been active recently (within 10 minutes)
     */
    fun isRecentlyActive(): Boolean {
        return lastActive?.let {
            val tenMinutesAgo = Date(System.currentTimeMillis() - 10 * 60 * 1000)
            return it.toDate().after(tenMinutesAgo)
        } ?: false
    }
}