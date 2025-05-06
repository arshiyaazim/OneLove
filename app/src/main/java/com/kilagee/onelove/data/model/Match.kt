package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing match status
 */
enum class MatchStatus {
    PENDING, MATCHED, REJECTED, EXPIRED, UNMATCHED
}

/**
 * Match data class for Firestore mapping
 */
data class Match(
    @DocumentId
    val id: String = "",
    
    // User IDs
    val userId1: String = "",
    val userId2: String = "",
    
    // User info
    val user1Name: String = "",
    val user2Name: String = "",
    val user1PhotoUrl: String? = null,
    val user2PhotoUrl: String? = null,
    
    // Match state
    val status: MatchStatus = MatchStatus.PENDING,
    val user1LikedUser2: Boolean = false,
    val user2LikedUser1: Boolean = false,
    val user1RejectedUser2: Boolean = false,
    val user2RejectedUser1: Boolean = false,
    
    // Communication
    val chatId: String? = null,
    val lastMessageTime: Timestamp? = null,
    val hasActiveOffers: Boolean = false,
    
    // Matching reason
    val matchScore: Double = 0.0,
    val matchReasons: List<String> = emptyList(),
    val commonInterests: List<String> = emptyList(),
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    
    val matchedAt: Timestamp? = null,
    val unmatchedAt: Timestamp? = null,
    
    // Notification status
    val user1Notified: Boolean = false,
    val user2Notified: Boolean = false,
    
    // Additional data
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if the match is mutual (both users liked each other)
     */
    fun isMutualMatch(): Boolean {
        return user1LikedUser2 && user2LikedUser1
    }
    
    /**
     * Check if the match is rejected (either user rejected the other)
     */
    fun isRejected(): Boolean {
        return user1RejectedUser2 || user2RejectedUser1
    }
    
    /**
     * Calculate the days since matching
     */
    fun daysSinceMatched(): Int {
        val matchTime = matchedAt?.toDate() ?: return 0
        val now = java.util.Date()
        val diff = now.time - matchTime.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * Get the other user's ID
     */
    fun getOtherUserId(userId: String): String {
        return if (userId == userId1) userId2 else userId1
    }
    
    /**
     * Get the other user's name
     */
    fun getOtherUserName(userId: String): String {
        return if (userId == userId1) user2Name else user1Name
    }
    
    /**
     * Get the other user's photo URL
     */
    fun getOtherUserPhotoUrl(userId: String): String? {
        return if (userId == userId1) user2PhotoUrl else user1PhotoUrl
    }
    
    /**
     * Check if the user has liked the other user
     */
    fun hasUserLikedOther(userId: String): Boolean {
        return if (userId == userId1) user1LikedUser2 else user2LikedUser1
    }
    
    /**
     * Check if the user has been liked by the other user
     */
    fun hasUserBeenLikedByOther(userId: String): Boolean {
        return if (userId == userId1) user2LikedUser1 else user1LikedUser2
    }
    
    /**
     * Check if the user has rejected the other user
     */
    fun hasUserRejectedOther(userId: String): Boolean {
        return if (userId == userId1) user1RejectedUser2 else user2RejectedUser1
    }
}