package com.kilagee.onelove.domain.model

import java.util.Date

/**
 * Domain model for User
 */
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val phoneNumber: String?,
    val profilePhotoUrl: String?,
    val bio: String?,
    val birthDate: Date?,
    val gender: String?,
    val location: GeoPoint?,
    val interests: List<String>,
    val isPremium: Boolean = false,
    val isVerified: Boolean = false,
    val lastActive: Date?,
    val createdAt: Date,
    val onlineStatus: OnlineStatus = OnlineStatus.OFFLINE,
    val verificationLevel: Int = 0,
    val walletBalance: Double = 0.0,
    val pointsBalance: Int = 0,
    val isProfileComplete: Boolean = false
)

/**
 * User online status
 */
enum class OnlineStatus {
    ONLINE,
    AWAY,
    OFFLINE
}

/**
 * Geographic point for location
 */
data class GeoPoint(
    val latitude: Double,
    val longitude: Double
)