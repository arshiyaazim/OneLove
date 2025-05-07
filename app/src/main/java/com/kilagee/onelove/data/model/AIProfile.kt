package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * AI Profile model for AI-generated conversation partners
 */
@Parcelize
data class AIProfile(
    val id: String = "",
    val name: String = "",
    val age: Int = 25,
    val gender: Gender = Gender.FEMALE,
    val bio: String = "",
    val photoUrls: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val personalityTraits: List<String> = emptyList(),
    val category: AIProfileCategory = AIProfileCategory.CASUAL,
    val languageLevel: LanguageLevel = LanguageLevel.FLUENT,
    val conversationStyle: ConversationStyle = ConversationStyle.FRIENDLY,
    val conversationTopics: List<String> = emptyList(),
    val responseDelay: IntRange = 2..10, // Seconds to delay response for realism
    val popularityScore: Int = 0, // 0-100 rating
    val isPremium: Boolean = false,
    val pointsCost: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val scriptTriggers: List<AIScriptTrigger> = emptyList(),
    val occupation: String = "",
    val education: String = "",
    val location: String = "",
    val relationshipStatus: RelationshipStatus = RelationshipStatus.SINGLE,
    val zodiacSign: ZodiacSign = ZodiacSign.UNSPECIFIED,
    val height: Int? = null, // in cm
    val tags: List<String> = emptyList(),
    val features: AIProfileFeatures = AIProfileFeatures()
) : Parcelable

/**
 * AI profile features
 */
@Parcelize
data class AIProfileFeatures(
    val canSendMedia: Boolean = false,
    val canInitiateConversation: Boolean = false,
    val canMakeOffers: Boolean = false,
    val canVoiceCall: Boolean = false,
    val canVideoCall: Boolean = false,
    val maxMessagesPerDay: Int = 50,
    val personalityDepth: Int = 1 // 1-5 rating for complexity
) : Parcelable

/**
 * AI profile category
 */
@Parcelize
enum class AIProfileCategory : Parcelable {
    CASUAL,
    ROMANTIC,
    FRIENDLY,
    PROFESSIONAL,
    CREATIVE,
    INTELLECTUAL,
    ADVENTUROUS,
    WELLNESS,
    SPIRITUAL,
    GAMING,
    CUSTOM
}

/**
 * Language level
 */
@Parcelize
enum class LanguageLevel : Parcelable {
    BASIC,
    INTERMEDIATE,
    ADVANCED,
    FLUENT,
    NATIVE
}

/**
 * Conversation style
 */
@Parcelize
enum class ConversationStyle : Parcelable {
    FRIENDLY,
    FLIRTY,
    SERIOUS,
    HUMOROUS,
    CURIOUS,
    SUPPORTIVE,
    CHALLENGING,
    MYSTERIOUS,
    PROFESSIONAL,
    CASUAL
}

/**
 * AI script trigger for programmed responses to specific message patterns
 */
@Parcelize
data class AIScriptTrigger(
    val id: String = "",
    val triggerType: TriggerType = TriggerType.KEYWORD,
    val pattern: String = "",
    val response: String = "",
    val priority: Int = 0,
    val isActive: Boolean = true,
    val cooldownMinutes: Int = 0,
    val category: String = "",
    val metadata: Map<String, String> = emptyMap()
) : Parcelable

/**
 * Trigger type for AI responses
 */
@Parcelize
enum class TriggerType : Parcelable {
    KEYWORD, // Triggered by specific keywords
    REGEX, // Triggered by regex pattern
    INTENT, // Triggered by detected user intent
    TOPIC, // Triggered by conversation topic
    TIME_ELAPSED, // Triggered after time elapsed
    MESSAGE_COUNT, // Triggered after n messages
    CONVERSATION_START, // Triggered at start
    CONVERSATION_END, // Triggered at end
    USER_EMOTION, // Triggered by detected emotion
    RANDOM // Random trigger with probability
}

/**
 * User AI interaction stats
 */
@Parcelize
data class UserAIInteractionStats(
    val userId: String = "",
    val aiProfileId: String = "",
    val messageCount: Int = 0,
    val lastInteractionAt: Date? = null,
    val favoriteTopics: List<String> = emptyList(),
    val unlockDate: Date? = null,
    val isPinned: Boolean = false,
    val isHidden: Boolean = false,
    val userRating: Int = 0, // 1-5 rating
    val updatedAt: Date = Date()
) : Parcelable