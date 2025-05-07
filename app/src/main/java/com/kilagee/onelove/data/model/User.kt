package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * User model representing a user in the application
 */
@Parcelize
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val bio: String = "",
    val birthDate: Date? = null,
    val gender: Gender = Gender.UNSPECIFIED,
    val interestedIn: List<Gender> = emptyList(),
    val location: GeoPoint? = null,
    val lastActive: Date = Date(),
    val isOnline: Boolean = false,
    val verificationStatus: VerificationStatus = VerificationStatus.NONE,
    val verificationTiers: List<VerificationTier> = emptyList(),
    val subscriptionType: SubscriptionType = SubscriptionType.FREE,
    val subscriptionExpiryDate: Date? = null,
    val pointsBalance: Int = 0,
    val matchPreferences: MatchPreferences = MatchPreferences(),
    val blockedUserIds: List<String> = emptyList(),
    val reportedUserIds: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isProfileComplete: Boolean = false,
    val profilePhotos: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val lookingFor: List<RelationshipType> = emptyList(),
    val height: Int? = null, // in cm
    val occupation: String = "",
    val education: String = "",
    val ethnicity: String = "",
    val religion: String = "",
    val languages: List<String> = emptyList(),
    val relationshipStatus: RelationshipStatus = RelationshipStatus.SINGLE,
    val hasChildren: Boolean? = null,
    val wantsChildren: Boolean? = null,
    val drinking: HabitFrequency = HabitFrequency.UNSPECIFIED,
    val smoking: HabitFrequency = HabitFrequency.UNSPECIFIED,
    val zodiacSign: ZodiacSign = ZodiacSign.UNSPECIFIED,
    val prompts: List<ProfilePrompt> = emptyList(),
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val fcmTokens: List<String> = emptyList(),
    val isAdmin: Boolean = false,
    val moderationStatus: ModerationStatus = ModerationStatus.APPROVED,
    val streak: Int = 0, // Days of consecutive app usage
    val profileVisitors: List<String> = emptyList(),
    val lastProfileVisitorsUpdatedAt: Date = Date()
) : Parcelable

/**
 * Gender enum representing gender options
 */
@Parcelize
enum class Gender : Parcelable {
    MALE,
    FEMALE,
    NON_BINARY,
    OTHER,
    UNSPECIFIED;
    
    companion object {
        fun fromString(value: String): Gender {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                UNSPECIFIED
            }
        }
    }
}

/**
 * GeoPoint representing a location
 */
@Parcelize
data class GeoPoint(
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(firebaseGeoPoint: com.google.firebase.firestore.GeoPoint) :
            this(firebaseGeoPoint.latitude, firebaseGeoPoint.longitude)
            
    fun toFirebaseGeoPoint(): com.google.firebase.firestore.GeoPoint {
        return com.google.firebase.firestore.GeoPoint(latitude, longitude)
    }
}

/**
 * Verification status enum
 */
@Parcelize
enum class VerificationStatus : Parcelable {
    NONE,
    PENDING,
    VERIFIED,
    REJECTED
}

/**
 * Verification tier enum
 */
@Parcelize
enum class VerificationTier : Parcelable {
    PHOTO,
    ID,
    BACKGROUND_CHECK,
    PHONE,
    EMAIL,
    EDUCATION,
    WORK,
    SOCIAL_MEDIA,
    CELEBRITY
}

/**
 * Subscription type enum
 */
@Parcelize
enum class SubscriptionType : Parcelable {
    FREE,
    BASIC,
    PREMIUM,
    VIP
}

/**
 * Subscription period enum
 */
@Parcelize
enum class SubscriptionPeriod : Parcelable {
    MONTHLY,
    QUARTERLY,
    YEARLY
}

/**
 * Match preferences
 */
@Parcelize
data class MatchPreferences(
    val ageRange: IntRange = 18..40,
    val distanceRange: Int = 50, // in km or miles based on distanceUnit
    val genderPreferences: List<Gender> = emptyList(),
    val relationshipTypes: List<RelationshipType> = emptyList()
) : Parcelable

/**
 * Relationship type enum
 */
@Parcelize
enum class RelationshipType : Parcelable {
    DATING,
    RELATIONSHIP,
    MARRIAGE,
    FRIENDSHIP,
    CASUAL,
    UNSPECIFIED
}

/**
 * Relationship status enum
 */
@Parcelize
enum class RelationshipStatus : Parcelable {
    SINGLE,
    DIVORCED,
    SEPARATED,
    WIDOWED,
    COMPLICATED,
    UNSPECIFIED
}

/**
 * Habit frequency enum
 */
@Parcelize
enum class HabitFrequency : Parcelable {
    NEVER,
    RARELY,
    SOMETIMES,
    OFTEN,
    REGULARLY,
    UNSPECIFIED
}

/**
 * Zodiac sign enum
 */
@Parcelize
enum class ZodiacSign : Parcelable {
    ARIES,
    TAURUS,
    GEMINI,
    CANCER,
    LEO,
    VIRGO,
    LIBRA,
    SCORPIO,
    SAGITTARIUS,
    CAPRICORN,
    AQUARIUS,
    PISCES,
    UNSPECIFIED
}

/**
 * Profile prompt
 */
@Parcelize
data class ProfilePrompt(
    val question: String,
    val answer: String
) : Parcelable

/**
 * Distance unit enum
 */
@Parcelize
enum class DistanceUnit : Parcelable {
    KILOMETERS,
    MILES
}

/**
 * Moderation status enum
 */
@Parcelize
enum class ModerationStatus : Parcelable {
    PENDING_REVIEW,
    APPROVED,
    RESTRICTED,
    BANNED
}