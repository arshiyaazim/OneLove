package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Entity representing user preferences for matching and app settings
 */
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val userId: String = "",
    
    // Matching preferences
    @ColumnInfo(name = "min_age_preference")
    val minAgePreference: Int? = null,
    
    @ColumnInfo(name = "max_age_preference")
    val maxAgePreference: Int? = null,
    
    @ColumnInfo(name = "gender_preference")
    val genderPreference: String? = null, // "Male", "Female", "Any"
    
    @ColumnInfo(name = "distance_preference_km")
    val distancePreferenceKm: Int = 50,
    
    @ColumnInfo(name = "interest_preferences")
    val interestPreferences: List<String> = emptyList(),
    
    // Notification preferences
    @ColumnInfo(name = "enable_match_notifications")
    val enableMatchNotifications: Boolean = true,
    
    @ColumnInfo(name = "enable_message_notifications")
    val enableMessageNotifications: Boolean = true,
    
    @ColumnInfo(name = "enable_offer_notifications")
    val enableOfferNotifications: Boolean = true,
    
    // Privacy preferences
    @ColumnInfo(name = "show_online_status")
    val showOnlineStatus: Boolean = true,
    
    @ColumnInfo(name = "show_distance")
    val showDistance: Boolean = true,
    
    @ColumnInfo(name = "show_age")
    val showAge: Boolean = true,
    
    // App preferences
    @ColumnInfo(name = "theme_preference")
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    
    @ColumnInfo(name = "language_preference")
    val languagePreference: String = "en" // ISO language code
)

enum class ThemePreference {
    LIGHT,
    DARK,
    SYSTEM
}