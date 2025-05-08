package com.kilagee.onelove.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * User status
 */
enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    DELETED
}

/**
 * Verification level
 */
enum class VerificationLevel {
    NONE,
    BASIC,
    PHOTO,
    ID,
    PREMIUM
}

/**
 * Gender options
 */
enum class Gender {
    MALE,
    FEMALE,
    NON_BINARY,
    OTHER,
    PREFER_NOT_TO_SAY
}

/**
 * Relationship preference
 */
enum class RelationshipPreference {
    FRIENDSHIP,
    DATING,
    RELATIONSHIP,
    MARRIAGE,
    CASUAL,
    NOT_SURE
}

/**
 * Subscription tier
 */
enum class SubscriptionTier {
    BASIC,
    PREMIUM,
    VIP
}

/**
 * User entity
 */
@Entity(tableName = "users")
@Parcelize
data class User(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    
    /**
     * User's display name
     */
    val name: String = "",
    
    /**
     * User's email address
     */
    val email: String = "",
    
    /**
     * User's age
     */
    val age: Int? = null,
    
    /**
     * User's gender
     */
    val gender: Gender? = null,
    
    /**
     * User's preferred gender(s) for dating/matching
     */
    val preferredGenders: List<Gender> = emptyList(),
    
    /**
     * Minimum age preference for matching
     */
    val minAgePreference: Int? = null,
    
    /**
     * Maximum age preference for matching
     */
    val maxAgePreference: Int? = null,
    
    /**
     * User's preferred relationship type(s)
     */
    val relationshipPreferences: List<RelationshipPreference> = emptyList(),
    
    /**
     * User's profile bio
     */
    val bio: String = "",
    
    /**
     * User's profile picture URL
     */
    val profilePictureUrl: String? = null,
    
    /**
     * Additional photos URLs
     */
    val photoUrls: List<String> = emptyList(),
    
    /**
     * User's location (city, state)
     */
    val location: String? = null,
    
    /**
     * User's latitude
     */
    val latitude: Double? = null,
    
    /**
     * User's longitude
     */
    val longitude: Double? = null,
    
    /**
     * User's occupation
     */
    val occupation: String? = null,
    
    /**
     * User's education
     */
    val education: String? = null,
    
    /**
     * User's interests
     */
    val interests: List<String> = emptyList(),
    
    /**
     * User's about me sections (key-value pairs for customizable profile sections)
     */
    val aboutMe: Map<String, String> = emptyMap(),
    
    /**
     * User's FCM token for push notifications
     */
    val fcmToken: String? = null,
    
    /**
     * Whether user is online
     */
    val isOnline: Boolean = false,
    
    /**
     * Last time user was online
     */
    val lastOnline: Timestamp? = null,
    
    /**
     * User's verification level
     */
    val verificationLevel: VerificationLevel = VerificationLevel.NONE,
    
    /**
     * User's subscription tier
     */
    val subscriptionTier: SubscriptionTier? = null,
    
    /**
     * Subscription expiration date
     */
    val subscriptionExpiresAt: Timestamp? = null,
    
    /**
     * User's rating (1-5)
     */
    val rating: Float? = null,
    
    /**
     * Number of ratings received
     */
    val ratingCount: Int = 0,
    
    /**
     * List of IDs of users this user has liked
     */
    val likedUserIds: List<String> = emptyList(),
    
    /**
     * List of IDs of users who have liked this user
     */
    val likedByUserIds: List<String> = emptyList(),
    
    /**
     * List of IDs of users this user has matched with
     */
    val matchedUserIds: List<String> = emptyList(),
    
    /**
     * List of IDs of users this user has blocked
     */
    val blockedUserIds: List<String> = emptyList(),
    
    /**
     * User's status
     */
    val status: UserStatus = UserStatus.ACTIVE,
    
    /**
     * Whether the user is an admin
     */
    val isAdmin: Boolean = false,
    
    /**
     * User's preferences for matching algorithm
     */
    val algorithmPreferences: Map<String, Any> = emptyMap(),
    
    /**
     * User's notification preferences
     */
    val notificationPreferences: Map<String, Boolean> = emptyMap(),
    
    /**
     * User's privacy settings
     */
    val privacySettings: Map<String, Boolean> = emptyMap(),
    
    /**
     * Maximum distance for matching (in kilometers)
     */
    val maxDistance: Int? = null,
    
    /**
     * Available coins for in-app purchases
     */
    val coins: Int = 0,
    
    /**
     * Last location update timestamp
     */
    val locationUpdatedAt: Timestamp? = null,
    
    /**
     * Account creation timestamp
     */
    val createdAt: Timestamp = Timestamp.now(),
    
    /**
     * Last profile update timestamp
     */
    val updatedAt: Timestamp = Timestamp.now()
) : Parcelable {
    
    /**
     * Check if user is verified
     */
    fun isVerified(): Boolean {
        return verificationLevel != VerificationLevel.NONE
    }
    
    /**
     * Check if user is premium
     */
    fun isPremium(): Boolean {
        return subscriptionTier != null && 
            (subscriptionExpiresAt == null || 
             subscriptionExpiresAt.toDate().after(Date()))
    }
    
    /**
     * Check if user has completed basic profile
     */
    fun hasCompletedBasicProfile(): Boolean {
        return name.isNotBlank() && 
            age != null && 
            gender != null && 
            bio.isNotBlank() && 
            profilePictureUrl != null
    }
    
    /**
     * Get user's display name (first name + initial)
     */
    fun getDisplayName(): String {
        val parts = name.split(" ")
        return if (parts.size > 1) {
            "${parts[0]} ${parts[1].first()}."
        } else {
            name
        }
    }
    
    /**
     * Get subscription days remaining
     */
    fun getSubscriptionDaysRemaining(): Int {
        if (subscriptionTier == null || subscriptionExpiresAt == null) {
            return 0
        }
        
        val now = Date().time
        val expiry = subscriptionExpiresAt.toDate().time
        
        if (now > expiry) {
            return 0
        }
        
        return ((expiry - now) / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * Check if location is available and recent (within 24 hours)
     */
    fun hasRecentLocation(): Boolean {
        if (latitude == null || longitude == null || locationUpdatedAt == null) {
            return false
        }
        
        val now = Date().time
        val lastUpdate = locationUpdatedAt.toDate().time
        val oneDayInMillis = 24 * 60 * 60 * 1000
        
        return (now - lastUpdate) < oneDayInMillis
    }
}