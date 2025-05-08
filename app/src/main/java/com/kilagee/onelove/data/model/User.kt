package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

/**
 * Verification level for user accounts
 */
enum class VerificationLevel {
    UNVERIFIED,    // Basic account with email verification only
    PENDING,       // Verification requested but not approved
    PHOTO,         // Photo verification approved
    ID,            // Identity document verification approved
    PREMIUM        // Higher level verification with premium subscription
}

/**
 * Relationship preferences
 */
enum class RelationshipPreference {
    DATING,
    FRIENDSHIP,
    CASUAL,
    RELATIONSHIP,
    MARRIAGE
}

/**
 * Subscription tiers
 */
enum class SubscriptionTier {
    FREE,          // Free tier with limited features
    BASIC,         // Basic paid tier
    PREMIUM,       // Premium tier with most features
    VIP            // VIP tier with all features and perks
}

/**
 * User activity status
 */
enum class UserStatus {
    ONLINE,
    AWAY,
    OFFLINE,
    DO_NOT_DISTURB
}

/**
 * Main user model that represents a user profile
 */
@Parcelize
data class User(
    @DocumentId
    val id: String = "",
    
    // Basic profile information
    val email: String = "",
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val bio: String = "",
    val phone: String? = null,
    
    // Profile photos (URLs)
    val profilePhotoUrl: String = "",
    val photoUrls: List<String> = emptyList(),
    
    // Location data
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String = "",
    
    // Preferences and interests
    val relationshipPreferences: List<RelationshipPreference> = emptyList(),
    val interests: List<String> = emptyList(),
    val minAgePreference: Int = 18,
    val maxAgePreference: Int = 50,
    val genderPreferences: List<String> = emptyList(),
    val maxDistance: Int = 50,
    
    // Verification and status
    val verificationLevel: VerificationLevel = VerificationLevel.UNVERIFIED,
    val isProfileComplete: Boolean = false,
    val status: UserStatus = UserStatus.OFFLINE,
    val lastActive: Timestamp = Timestamp.now(),
    
    // Subscription information
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val subscriptionExpiryDate: Timestamp? = null,
    val points: Int = 0,
    
    // Security and analytics
    val deviceTokens: List<String> = emptyList(),
    @PropertyName("created_at")
    val createdAt: Timestamp = Timestamp.now(),
    @PropertyName("updated_at") 
    val updatedAt: Timestamp = Timestamp.now(),
    
    // Engagement metrics
    val matchCount: Int = 0,
    val messageCount: Int = 0,
    val profileViewCount: Int = 0,
    val likeCount: Int = 0,
    
    // Admin fields
    val isAdmin: Boolean = false,
    val isModerated: Boolean = false,
    val isBanned: Boolean = false
) : Parcelable