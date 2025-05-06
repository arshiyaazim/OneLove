package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import java.util.Locale

/**
 * Enum representing user gender
 */
enum class UserGender {
    MALE, FEMALE, NON_BINARY, OTHER, PREFER_NOT_TO_SAY;
    
    fun getLocalizedName(locale: Locale = Locale.getDefault()): String {
        return when (this) {
            MALE -> when (locale.language) {
                "es" -> "Hombre"
                "fr" -> "Homme"
                else -> "Male"
            }
            FEMALE -> when (locale.language) {
                "es" -> "Mujer"
                "fr" -> "Femme"
                else -> "Female"
            }
            NON_BINARY -> when (locale.language) {
                "es" -> "No binario"
                "fr" -> "Non-binaire"
                else -> "Non-binary"
            }
            OTHER -> when (locale.language) {
                "es" -> "Otro"
                "fr" -> "Autre"
                else -> "Other"
            }
            PREFER_NOT_TO_SAY -> when (locale.language) {
                "es" -> "Prefiero no decirlo"
                "fr" -> "Je préfère ne pas le dire"
                else -> "Prefer not to say"
            }
        }
    }
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
    FREE, BASIC, PREMIUM, VIP;
    
    fun getFeatures(): List<PremiumFeature> {
        return when (this) {
            FREE -> listOf(
                PremiumFeature.BASIC_MATCHING,
                PremiumFeature.LIMITED_MESSAGES
            )
            BASIC -> listOf(
                PremiumFeature.BASIC_MATCHING,
                PremiumFeature.UNLIMITED_MESSAGES,
                PremiumFeature.SEE_LIKES,
                PremiumFeature.ADVANCED_FILTERS
            )
            PREMIUM -> listOf(
                PremiumFeature.BASIC_MATCHING,
                PremiumFeature.UNLIMITED_MESSAGES,
                PremiumFeature.SEE_LIKES,
                PremiumFeature.ADVANCED_FILTERS,
                PremiumFeature.PROFILE_BOOST,
                PremiumFeature.VIDEO_CALLS,
                PremiumFeature.READ_RECEIPTS,
                PremiumFeature.UNLIMITED_OFFERS
            )
            VIP -> PremiumFeature.values().toList()
        }
    }
}

/**
 * Enum representing premium features
 */
enum class PremiumFeature {
    BASIC_MATCHING,           // Available to all users
    LIMITED_MESSAGES,         // Available to all users
    UNLIMITED_MESSAGES,       // Basic and above
    SEE_LIKES,                // Basic and above
    ADVANCED_FILTERS,         // Basic and above
    PROFILE_BOOST,            // Premium and above
    VIDEO_CALLS,              // Premium and above
    READ_RECEIPTS,            // Premium and above
    UNLIMITED_OFFERS,         // Premium and above
    AI_CHAT,                  // VIP only
    VERIFIED_BADGE,           // VIP only
    PRIORITY_SUPPORT,         // VIP only
    INCOGNITO_MODE            // VIP only
}

/**
 * Enum representing report reason
 */
enum class ReportReason {
    FAKE_PROFILE,
    INAPPROPRIATE_CONTENT,
    HARASSMENT,
    SPAM,
    UNDERAGE,
    IMPERSONATION,
    OTHER
}

/**
 * Sealed class representing user preferences
 */
sealed class UserPreference {
    data class DistancePreference(val maxDistanceKm: Int) : UserPreference()
    data class AgeRangePreference(val minAge: Int, val maxAge: Int) : UserPreference()
    data class GenderPreference(val genders: List<UserGender>) : UserPreference()
    data class VerifiedOnlyPreference(val verifiedOnly: Boolean) : UserPreference()
    data class InterestPreference(val interests: List<String>) : UserPreference()
    data class LanguagePreference(val preferredLanguages: List<String>) : UserPreference()
    data class ActiveRecentlyPreference(val mustBeActiveRecently: Boolean) : UserPreference()
}

/**
 * Data class for regional settings
 */
data class RegionalSettings(
    val locale: String = "en_US",
    val timeZone: String = "UTC",
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val dateFormat: DateFormat = DateFormat.MDY
)

/**
 * Enum for distance units
 */
enum class DistanceUnit {
    KILOMETERS, MILES
}

/**
 * Enum for date formats
 */
enum class DateFormat {
    MDY, DMY, YMD
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
    val languages: List<String> = listOf("en"),
    val occupation: String = "",
    val education: String = "",
    
    // Profile media
    val profilePhotoUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val photos: List<String> = emptyList(),
    val verifiedPhotoUrl: String? = null,
    
    // Stats and metrics
    val points: Int = 0,
    val matchesCount: Int = 0,
    val likesCount: Int = 0,
    val likesReceivedCount: Int = 0,
    val offersCount: Int = 0,
    val popularity: Int = 0, // 0-100 scale based on activity and engagement
    val activeDays: Int = 0,
    
    // Status
    val isOnline: Boolean = false,
    val isPremium: Boolean = false,
    val isVerified: Boolean = false,
    val isLiked: Boolean = false,
    val isAdmin: Boolean = false,
    val isBanned: Boolean = false,
    val isIncognito: Boolean = false,
    val verificationStatus: VerificationStatus = VerificationStatus.NOT_VERIFIED,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val subscriptionExpiryDate: Date? = null,
    val verificationDocuments: List<String> = emptyList(),
    
    // Premium features
    val activeBoost: Boolean = false,
    val boostExpiryTime: Timestamp? = null,
    val availableBoosts: Int = 0,
    val canSeeWhoLikedThem: Boolean = false,
    val canUseAdvancedFilters: Boolean = false,
    val canSendUnlimitedMessages: Boolean = false,
    val dailyMessageLimit: Int = 10, // For non-premium users
    val remainingDailyMessages: Int = 10,
    val lastMessageLimitReset: Timestamp? = null,
    
    // Settings
    val showLocation: Boolean = true,
    val showOnlineStatus: Boolean = true,
    val showLastActive: Boolean = true,
    val notificationEnabled: Boolean = true,
    val emailNotificationEnabled: Boolean = true,
    val profileVisibility: Boolean = true,
    val maxDistanceInKm: Int = 100,
    val minAgePreference: Int = 18,
    val maxAgePreference: Int = 99,
    val language: String = "en",
    val regionalSettings: RegionalSettings = RegionalSettings(),
    val advancedFilters: Map<String, Any> = emptyMap(),
    
    // Relationships and blocks
    val blockedUsers: List<String> = emptyList(),
    val reportedUsers: Map<String, ReportReason> = emptyMap(),
    val favoriteUsers: List<String> = emptyList(),
    val usersWhoLiked: List<String> = emptyList(),
    val usersLiked: List<String> = emptyList(),
    
    // Device info
    val fcmTokens: List<String> = emptyList(),
    val lastKnownDevice: String? = null,
    val appVersion: String? = null,
    val platform: String? = null,
    
    // AI Chat Interaction
    val aiChatEnabled: Boolean = false,
    val favoriteAiProfiles: List<String> = emptyList(),
    val aiChatCredits: Int = 0,
    val aiInteractionCount: Int = 0,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val lastActive: Timestamp? = null,
    
    val lastBoostTime: Timestamp? = null,
    val lastLocationUpdate: Timestamp? = null,
    val lastProfileEdit: Timestamp? = null,
    
    // Extra fields
    @get:PropertyName("extraData")
    @set:PropertyName("extraData")
    var extraData: Map<String, Any> = emptyMap()
) {
    /**
     * Calculate user age from birth date
     */
    @Exclude
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
    @Exclude
    fun hasActiveSubscription(): Boolean {
        return isPremium && subscriptionTier != SubscriptionTier.FREE && 
                (subscriptionExpiryDate == null || subscriptionExpiryDate.after(Date()))
    }
    
    /**
     * Days remaining in subscription
     */
    @Exclude
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
    @Exclude
    fun isRecentlyActive(): Boolean {
        return lastActive?.let {
            val tenMinutesAgo = Date(System.currentTimeMillis() - 10 * 60 * 1000)
            return it.toDate().after(tenMinutesAgo)
        } ?: false
    }
    
    /**
     * Check if a user is blocked
     */
    @Exclude
    fun isUserBlocked(userId: String): Boolean {
        return blockedUsers.contains(userId)
    }
    
    /**
     * Check if a user has been reported
     */
    @Exclude
    fun isUserReported(userId: String): Boolean {
        return reportedUsers.containsKey(userId)
    }
    
    /**
     * Check if user has a specific premium feature
     */
    @Exclude
    fun hasPremiumFeature(feature: PremiumFeature): Boolean {
        return hasActiveSubscription() && subscriptionTier.getFeatures().contains(feature)
    }
    
    /**
     * Check if user has available message credits (for non-premium)
     */
    @Exclude
    fun hasAvailableMessages(): Boolean {
        if (hasPremiumFeature(PremiumFeature.UNLIMITED_MESSAGES)) {
            return true
        }
        
        // Check if message limit needs to be reset (daily)
        val now = Date()
        lastMessageLimitReset?.toDate()?.let { lastReset ->
            // Check if it's a new day since the last reset
            val calendar1 = java.util.Calendar.getInstance()
            val calendar2 = java.util.Calendar.getInstance()
            calendar1.time = now
            calendar2.time = lastReset
            
            val sameDay = calendar1.get(java.util.Calendar.YEAR) == calendar2.get(java.util.Calendar.YEAR) &&
                    calendar1.get(java.util.Calendar.DAY_OF_YEAR) == calendar2.get(java.util.Calendar.DAY_OF_YEAR)
            
            // If it's a new day, user would have full message limit
            if (!sameDay) {
                return true
            }
        }
        
        return remainingDailyMessages > 0
    }
    
    /**
     * Get time until boost expires
     */
    @Exclude
    fun getBoostTimeRemaining(): Long {
        val now = Date()
        return boostExpiryTime?.toDate()?.time?.minus(now.time)?.coerceAtLeast(0) ?: 0
    }
    
    /**
     * Check if boost is active
     */
    @Exclude
    fun isBoostActive(): Boolean {
        return activeBoost && getBoostTimeRemaining() > 0
    }
    
    /**
     * Get preferred distance unit
     */
    @Exclude
    fun getPreferredDistanceUnit(): DistanceUnit {
        return regionalSettings.distanceUnit
    }
    
    /**
     * Format distance according to user's regional preferences
     */
    @Exclude
    fun formatDistance(distanceKm: Double): String {
        val unit = getPreferredDistanceUnit()
        return when (unit) {
            DistanceUnit.KILOMETERS -> "${distanceKm.toInt()} km"
            DistanceUnit.MILES -> "${(distanceKm * 0.621371).toInt()} mi"
        }
    }
    
    /**
     * Get all premium features available to this user
     */
    @Exclude
    fun getAvailablePremiumFeatures(): List<PremiumFeature> {
        return if (hasActiveSubscription()) {
            subscriptionTier.getFeatures()
        } else {
            listOf(PremiumFeature.BASIC_MATCHING, PremiumFeature.LIMITED_MESSAGES)
        }
    }
}