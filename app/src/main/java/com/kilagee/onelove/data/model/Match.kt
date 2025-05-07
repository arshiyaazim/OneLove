package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Match model representing a match between two users
 */
@Parcelize
data class Match(
    val id: String = "",
    val userIds: List<String> = emptyList(),
    val user1Id: String = "",
    val user2Id: String = "",
    val user1LikedAt: Date? = null,
    val user2LikedAt: Date? = null,
    val matchedAt: Date? = null,
    val lastMessageAt: Date? = null,
    val lastMessageSenderId: String = "",
    val lastMessageText: String = "",
    val lastMessageType: MessageType = MessageType.TEXT,
    val unreadCountUser1: Int = 0,
    val unreadCountUser2: Int = 0,
    val isActive: Boolean = true,
    val userIds1BlockedUser2: Boolean = false,
    val userIds2BlockedUser1: Boolean = false,
    val compatibilityScore: Int = 0, // 0-100
    val aiSuggestedTopics: List<String> = emptyList(),
    val matchType: MatchType = MatchType.REGULAR,
    val expiresAt: Date? = null, // For temporary matches (e.g., premium feature)
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

/**
 * Match type enum
 */
@Parcelize
enum class MatchType : Parcelable {
    REGULAR, // Regular match between two users
    BOOST, // Match created during boost
    TOP_PICK, // System-selected top picks
    AI_PROFILE, // Match with AI profile
    RECONNECT, // Reconnected match
    SUPER_LIKE // Match created from super like
}

/**
 * Match user state representing a user's state in a match
 */
@Parcelize
data class MatchUserState(
    val userId: String,
    val matchId: String,
    val isOnline: Boolean = false,
    val lastActive: Date = Date(),
    val isTyping: Boolean = false,
    val typingLastUpdated: Date = Date(),
    val hasUnreadMessages: Boolean = false,
    val unreadCount: Int = 0,
    val lastReadMessageId: String = "",
    val updatedAt: Date = Date()
) : Parcelable

/**
 * Match activity representing an activity in a match
 */
@Parcelize
data class MatchActivity(
    val id: String = "",
    val matchId: String = "",
    val userId: String = "",
    val activityType: MatchActivityType = MatchActivityType.VIEW_PROFILE,
    val createdAt: Date = Date(),
    val metadata: Map<String, String> = emptyMap()
) : Parcelable

/**
 * Match activity type enum
 */
@Parcelize
enum class MatchActivityType : Parcelable {
    VIEW_PROFILE,
    SEND_MESSAGE,
    SEND_MEDIA,
    CALL_INITIATED,
    CALL_MISSED,
    CALL_COMPLETED,
    SEND_GIFT,
    CREATE_OFFER,
    ACCEPT_OFFER,
    DECLINE_OFFER,
    REACT_TO_MESSAGE,
    VISIT_PROFILE,
    UNMATCH
}

/**
 * Match recommendation representing a recommendation for a potential match
 */
@Parcelize
data class MatchRecommendation(
    val id: String = "",
    val userId: String = "",
    val recommendedUserId: String = "",
    val score: Double = 0.0, // 0.0-1.0
    val reasonCodes: List<RecommendationReason> = emptyList(),
    val isViewed: Boolean = false,
    val viewedAt: Date? = null,
    val action: UserAction? = null,
    val actionTakenAt: Date? = null,
    val createdAt: Date = Date(),
    val expiresAt: Date? = null
) : Parcelable

/**
 * User action enum
 */
@Parcelize
enum class UserAction : Parcelable {
    LIKE,
    PASS,
    SUPER_LIKE,
    BLOCK,
    REPORT
}

/**
 * Recommendation reason enum
 */
@Parcelize
enum class RecommendationReason : Parcelable {
    COMMON_INTERESTS,
    LOCATION_PROXIMITY,
    SIMILAR_AGE,
    MUTUAL_FRIENDS,
    SIMILAR_EDUCATION,
    SIMILAR_PROFESSION,
    SIMILAR_LIFESTYLE,
    COMPATIBLE_RELATIONSHIP_GOALS,
    POPULARITY,
    ACTIVITY_LEVEL,
    AI_SUGGESTED
}