package com.kilagee.onelove.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Match status
 */
enum class MatchStatus {
    ACTIVE,
    EXPIRED,
    CANCELLED,
    BLOCKED
}

/**
 * Match entity representing a connection between two users
 */
@Entity(tableName = "matches")
@Parcelize
data class Match(
    @PrimaryKey
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    
    /**
     * IDs of the matched users (always 2 users)
     */
    val userIds: List<String> = emptyList(),
    
    /**
     * ID of the chat associated with this match
     */
    val chatId: String? = null,
    
    /**
     * Status of the match
     */
    val status: MatchStatus = MatchStatus.ACTIVE,
    
    /**
     * Strength of the match (0-100)
     */
    val matchStrength: Int? = null,
    
    /**
     * Reasons for the match (e.g., common interests)
     */
    val matchReasons: List<String> = emptyList(),
    
    /**
     * ID of the user who initiated the match (if applicable)
     */
    val initiatorId: String? = null,
    
    /**
     * Whether both users have messaged each other
     */
    val hasBothMessaged: Boolean = false,
    
    /**
     * Custom metadata for the match
     */
    val metadata: Map<String, String> = emptyMap(),
    
    /**
     * Whether the match was created by AI recommendation
     */
    val isAiRecommended: Boolean = false,
    
    /**
     * Timestamp when the match was created
     */
    val createdAt: Timestamp = Timestamp.now(),
    
    /**
     * Timestamp when the match was last updated
     */
    val updatedAt: Timestamp = Timestamp.now(),
    
    /**
     * Timestamp when the match expires (if applicable)
     */
    val expiresAt: Timestamp? = null,
    
    /**
     * Timestamp when the match was cancelled (if applicable)
     */
    val cancelledAt: Timestamp? = null,
    
    /**
     * ID of the user who cancelled the match (if applicable)
     */
    val cancelledBy: String? = null
) : Parcelable {
    
    /**
     * Get the other user in the match
     * 
     * @param currentUserId ID of the current user
     * @return ID of the other user or null if not found
     */
    fun getOtherUserId(currentUserId: String): String? {
        if (userIds.size != 2) {
            return null
        }
        
        return userIds.firstOrNull { it != currentUserId }
    }
    
    /**
     * Check if the match is active
     * 
     * @return True if the match is active
     */
    fun isActive(): Boolean {
        return status == MatchStatus.ACTIVE && 
            (expiresAt == null || expiresAt.toDate().time > System.currentTimeMillis())
    }
    
    /**
     * Check if the match has expired
     * 
     * @return True if the match has expired
     */
    fun isExpired(): Boolean {
        return status == MatchStatus.EXPIRED || 
            (expiresAt != null && expiresAt.toDate().time <= System.currentTimeMillis())
    }
    
    /**
     * Check if the match has been cancelled
     * 
     * @return True if the match has been cancelled
     */
    fun isCancelled(): Boolean {
        return status == MatchStatus.CANCELLED
    }
    
    /**
     * Check if the match has been blocked
     * 
     * @return True if the match has been blocked
     */
    fun isBlocked(): Boolean {
        return status == MatchStatus.BLOCKED
    }
    
    /**
     * Check if the match is new (less than 24 hours old)
     * 
     * @return True if the match is new
     */
    fun isNew(): Boolean {
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
        return System.currentTimeMillis() - createdAt.toDate().time < twentyFourHoursInMillis
    }
    
    /**
     * Get match age in days
     * 
     * @return Age of the match in days
     */
    fun getMatchAgeInDays(): Int {
        val matchTime = createdAt.toDate().time
        val now = System.currentTimeMillis()
        val diffInMillis = now - matchTime
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }
}