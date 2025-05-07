package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Data class representing a user in the application
 */
@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val age: Int,
    val gender: String,
    val bio: String? = null,
    val height: Int? = null, // in cm
    val occupation: String? = null,
    val education: String? = null,
    val interests: List<String> = emptyList(),
    val profileImageUrls: List<String> = emptyList(),
    val location: Location? = null,
    val preferences: UserPreferences? = null,
    val isVerified: Boolean = false,
    val verificationLevel: Int = 0, // 0: None, 1: Email, 2: Phone, 3: ID, 4: Social Media
    val isPremium: Boolean = false,
    val premiumTier: String? = null, // "basic", "gold", "platinum"
    val premiumExpiresAt: Date? = null,
    val createdAt: Date,
    val lastActive: Date? = null,
    val lastLocationUpdate: Date? = null,
    val points: Int = 0,
    val isOnline: Boolean = false,
    val blockedUsers: List<String> = emptyList(),
    val profileSettings: ProfileSettings? = null,
    val notificationSettings: NotificationSettings? = null,
    val privacySettings: PrivacySettings? = null,
    val profileCompletion: Int = 0, // Percentage of profile completion
    val socialLinks: Map<String, String> = emptyMap() // platform -> url
) : Parcelable

/**
 * Data class representing a user's location
 */
@Parcelize
data class Location(
    val latitude: Double,
    val longitude: Double,
    val city: String? = null,
    val country: String? = null,
    val formattedAddress: String? = null,
    val lastUpdated: Date? = null
) : Parcelable

/**
 * Data class representing user preferences for discovery
 */
@Parcelize
data class UserPreferences(
    val minAge: Int = 18,
    val maxAge: Int = 99,
    val maxDistance: Int = 50, // in km
    val genderPreferences: List<String> = emptyList(),
    val showMe: Boolean = true,
    val autoPlayVideos: Boolean = true,
    val showOnlineStatus: Boolean = true,
    val showLastActive: Boolean = true
) : Parcelable

/**
 * Data class representing profile settings
 */
@Parcelize
data class ProfileSettings(
    val showAge: Boolean = true,
    val showDistance: Boolean = true,
    val showLastActive: Boolean = true,
    val showOnlineStatus: Boolean = true
) : Parcelable

/**
 * Data class representing notification settings
 */
@Parcelize
data class NotificationSettings(
    val newMatches: Boolean = true,
    val messages: Boolean = true,
    val messageReplies: Boolean = true,
    val superLikes: Boolean = true,
    val appUpdates: Boolean = true,
    val offers: Boolean = true,
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val callNotifications: Boolean = true,
    val vibrate: Boolean = true,
    val sound: Boolean = true
) : Parcelable

/**
 * Data class representing privacy settings
 */
@Parcelize
data class PrivacySettings(
    val showMeInDiscovery: Boolean = true,
    val showOnlineStatus: Boolean = true,
    val showReadReceipts: Boolean = true,
    val showLastActive: Boolean = true,
    val allowLocationTracking: Boolean = true,
    val dataSharing: Boolean = true,
    val allowAnalytics: Boolean = true
) : Parcelable

/**
 * Data class representing actions taken on a user
 */
@Parcelize
data class UserAction(
    val userId: String,
    val actionType: ActionType,
    val timestamp: Date,
    val note: String? = null
) : Parcelable

/**
 * Enum representing types of actions that can be taken on users
 */
@Parcelize
enum class ActionType : Parcelable {
    LIKE,
    SUPER_LIKE,
    PASS,
    BLOCK,
    REPORT,
    MESSAGE,
    MATCH
}