package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Represents an AI profile in the app
 */
@Parcelize
data class AIProfile(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val bio: String = "",
    val personality: String = "", // Detailed personality description
    val userId: String = "", // Associated user ID in the users collection
    val photoUrls: List<String> = listOf(),
    val interests: List<String> = listOf(),
    val responsePatterns: List<ResponsePattern> = listOf(),
    val conversationScripts: List<ConversationScript> = listOf(),
    val behaviorRules: List<String> = listOf(),
    val category: AIProfileCategory = AIProfileCategory.GENERAL,
    val isPremium: Boolean = false, // Requires Premium subscription
    val pointsCost: Int = 0, // Points cost to unlock if not premium
    val popularity: Int = 0, // Popularity rating (for sorting)
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

/**
 * Defines a response pattern for AI messages
 */
@Parcelize
data class ResponsePattern(
    val id: String = "",
    val triggers: List<String> = listOf(), // Keywords or phrases that trigger this response
    val responses: List<String> = listOf(), // Possible responses to choose from
    val priority: Int = 0, // Higher priority patterns take precedence
    val responseDelay: IntRange = 1..5, // Delay range in seconds
    val contextTags: List<String> = listOf() // Tags for contextual relevance
) : Parcelable

/**
 * Defines a scripted conversation path for AI profiles
 */
@Parcelize
data class ConversationScript(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val triggers: List<String> = listOf(), // Keywords that activate this script
    val steps: List<ConversationStep> = listOf(),
    val isLooping: Boolean = false, // Whether to loop back to start when finished
    val cooldownHours: Int = 24, // Hours before this script can be triggered again
    val minEngagementLevel: Int = 0 // Minimum user engagement level required (0-5)
) : Parcelable

/**
 * Step in a scripted conversation
 */
@Parcelize
data class ConversationStep(
    val id: String = "",
    val messages: List<String> = listOf(), // Possible messages to choose from
    val expectedResponseTypes: List<String> = listOf(), // Types of user responses expected
    val nextStepConditions: List<StepCondition> = listOf(), // Conditions for next step
    val fallbackStepId: String = "", // Step to go to if no conditions match
    val delaySeconds: IntRange = 1..5, // Delay range in seconds
    val attachmentType: AttachmentType = AttachmentType.NONE, // Type of attachment
    val attachmentUrl: String = "" // URL to attachment if any
) : Parcelable

/**
 * Condition for determining next step in conversation
 */
@Parcelize
data class StepCondition(
    val id: String = "",
    val keywords: List<String> = listOf(), // Keywords to match in user response
    val sentimentType: SentimentType = SentimentType.ANY, // Expected sentiment
    val nextStepId: String = "" // Next step ID if condition matches
) : Parcelable

enum class AIProfileCategory {
    ROMANTIC, FRIEND, MENTOR, ADVENTURE, CREATIVE, GENERAL
}

enum class AttachmentType {
    NONE, IMAGE, AUDIO, VIDEO, LOCATION, OFFER
}

enum class SentimentType {
    POSITIVE, NEGATIVE, NEUTRAL, ANY
}