package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize

/**
 * User data model
 * Represents a user in the OneLove application
 */
@Parcelize
data class User(
    @DocumentId val id: String = "",
    val email: String = "",
    val name: String = "",
    val birthday: Timestamp? = null,
    val gender: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val occupation: String = "",
    val education: String = "",
    val interests: List<String> = emptyList(),
    val photos: List<String> = emptyList(), // URLs to photo storage
    val location: GeoPoint? = null,
    val lastActive: Timestamp? = null,
    val joinDate: Timestamp? = null,
    val isPremium: Boolean = false,
    val premiumExpiry: Timestamp? = null,
    val points: Int = 0,
    val verificationStatus: String = "UNVERIFIED", // UNVERIFIED, PENDING, VERIFIED, REJECTED
    val verificationLevel: Int = 0, // 0-3 based on ID, phone, social, etc.
    val isActive: Boolean = true,
    val blockedUsers: List<String> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val preferredAgeRange: IntArray = intArrayOf(18, 50),
    val preferredDistance: Int = 50, // in kilometers
    val preferredGenders: List<String> = listOf("MALE", "FEMALE", "NON_BINARY"),
    val hideProfile: Boolean = false,
    val fcmTokens: List<String> = emptyList(), // Push notification tokens
) : Parcelable {
    companion object {
        const val VERIFICATION_UNVERIFIED = "UNVERIFIED"
        const val VERIFICATION_PENDING = "PENDING"
        const val VERIFICATION_VERIFIED = "VERIFIED"
        const val VERIFICATION_REJECTED = "REJECTED"
        
        const val GENDER_MALE = "MALE"
        const val GENDER_FEMALE = "FEMALE"
        const val GENDER_NON_BINARY = "NON_BINARY"
        const val GENDER_OTHER = "OTHER"
    }
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as User
        
        if (id != other.id) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * User settings data model
 * Contains user-configurable settings
 */
@Parcelize
data class UserSettings(
    val darkMode: Boolean = false,
    val showOnlineStatus: Boolean = true,
    val showDistance: Boolean = true,
    val showAge: Boolean = true,
    val allowMessages: Boolean = true,
    val allowNotifications: Boolean = true,
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val distanceUnit: String = "KM", // KM or MILES
    val language: String = "en",
) : Parcelable