package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Types of user interactions tracked for analytics and personalization
 */
enum class InteractionType {
    MESSAGE_SENT,
    MESSAGE_READ,
    PROFILE_VIEW,
    LIKE,
    DISLIKE,
    MATCH_ACCEPTED,
    CALL_INITIATED,
    CALL_ACCEPTED,
    CALL_DECLINED,
    CALL_ENDED,
    PROFILE_EDIT,
    APP_OPEN,
    APP_CLOSE,
    SEARCH,
    FILTER_CHANGE,
    SUBSCRIPTION_VIEW,
    IN_APP_PURCHASE,
    FEATURE_USAGE,
    FEED_SCROLL,
    NOTIFICATION_OPEN
}

/**
 * Entity that tracks user interactions with the app
 * Used for personalization, analytics, and notification prioritization
 */
@Entity(tableName = "user_interactions")
data class UserInteraction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    /**
     * Type of interaction
     */
    val type: InteractionType,
    
    /**
     * ID of the user who is the target of this interaction (optional)
     */
    val userId: String? = null,
    
    /**
     * Timestamp when this interaction occurred
     */
    val timestamp: Long = System.currentTimeMillis(),
    
    /**
     * Duration of the interaction in milliseconds (if applicable)
     */
    val durationMs: Long? = null,
    
    /**
     * Screen or feature where the interaction occurred
     */
    val source: String? = null,
    
    /**
     * Additional metadata as JSON string
     */
    val metadata: String? = null
)