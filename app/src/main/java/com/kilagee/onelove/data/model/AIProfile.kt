package com.kilagee.onelove.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.util.Date

/**
 * Data class representing an AI Profile in the OneLove app
 */
data class AIProfile(
    @DocumentId
    val id: String = "",
    
    val name: String = "",
    val age: Int = 0,
    val gender: Gender = Gender.FEMALE,
    val country: String = "",
    val city: String = "",
    
    val personalityType: PersonalityType = PersonalityType.ROMANTIC,
    val personalityTags: List<String> = listOf(),
    
    val bio: String = "",
    val interests: List<String> = listOf(),
    val occupation: String = "",
    val education: String? = null,
    
    val photoUrl: String = "",
    val photoUrls: List<String> = listOf(),
    
    val responseStyles: Map<ResponseStyle, Int> = mapOf(),
    val languageLevel: LanguageLevel = LanguageLevel.NATIVE,
    
    @PropertyName("created_at")
    val createdAt: Date = Date(),
    
    @PropertyName("is_active")
    val isActive: Boolean = true,
    
    @PropertyName("match_rate")
    val matchRate: Double = 0.5, // How often this profile is matched with users
    
    @PropertyName("message_delay_min")
    val messageDelayMin: Int = 30, // Minimum seconds to wait before responding
    
    @PropertyName("message_delay_max")
    val messageDelayMax: Int = 300, // Maximum seconds to wait before responding
    
    @PropertyName("max_daily_messages")
    val maxDailyMessages: Int = 50, // Maximum messages this AI can send per day
    
    @PropertyName("response_pool_id")
    val responsePoolId: String = "", // ID of the response pool for this profile
    
    // Additional metadata
    val height: Int? = null, // Height in cm
    val relationshipStatus: RelationshipStatus? = null,
    val drinking: Frequency? = null,
    val smoking: Frequency? = null,
    val children: ChildrenStatus? = null,
    val religion: Religion? = null,
    val zodiacSign: ZodiacSign? = null,
    
    // Specific AI settings
    @PropertyName("conversation_depth")
    val conversationDepth: Int = 2, // How many previous messages to consider for context
    
    @PropertyName("personality_strength")
    val personalityStrength: Double = 0.7, // How strongly to maintain the personality (0.0-1.0)
    
    @PropertyName("response_variety")
    val responseVariety: Double = 0.6, // How varied the responses should be (0.0-1.0)
    
    @PropertyName("emotional_range")
    val emotionalRange: Double = 0.5, // How much emotional variation to show (0.0-1.0)
    
    @PropertyName("emoji_frequency")
    val emojiFrequency: Double = 0.3, // How often to use emojis (0.0-1.0)
    
    @PropertyName("message_length_min")
    val messageLengthMin: Int = 10, // Minimum message length in characters
    
    @PropertyName("message_length_max")
    val messageLengthMax: Int = 200 // Maximum message length in characters
)

/**
 * Personality types for AI profiles
 */
enum class PersonalityType {
    ROMANTIC,
    FLIRTY,
    INTELLECTUAL,
    ADVENTUROUS,
    SHY,
    CONFIDENT,
    HUMOROUS,
    MYSTERIOUS,
    CARING,
    CREATIVE,
    AMBITIOUS,
    SPIRITUAL,
    PRACTICAL,
    OUTGOING,
    RESERVED
}

/**
 * Response styles used by AI profiles
 */
enum class ResponseStyle {
    FLIRTY,
    ROMANTIC,
    HUMOROUS,
    INTELLECTUAL,
    SUPPORTIVE,
    CASUAL,
    DEEP,
    PLAYFUL,
    SERIOUS
}

/**
 * Language proficiency levels
 */
enum class LanguageLevel {
    NATIVE,
    FLUENT,
    INTERMEDIATE,
    BASIC
}

/**
 * Relationship statuses
 */
enum class RelationshipStatus {
    SINGLE,
    DIVORCED,
    WIDOWED,
    SEPARATED,
    COMPLICATED
}

/**
 * Frequency options for habits
 */
enum class Frequency {
    NEVER,
    RARELY,
    SOCIALLY,
    REGULARLY,
    FREQUENTLY
}

/**
 * Children status options
 */
enum class ChildrenStatus {
    NO_CHILDREN,
    HAVE_CHILDREN_NOT_AT_HOME,
    HAVE_CHILDREN_AT_HOME,
    WANT_CHILDREN,
    DONT_WANT_CHILDREN
}

/**
 * Religion options
 */
enum class Religion {
    AGNOSTIC,
    ATHEIST,
    BUDDHIST,
    CHRISTIAN,
    HINDU,
    JEWISH,
    MUSLIM,
    SPIRITUAL,
    OTHER,
    PREFER_NOT_TO_SAY
}

/**
 * Zodiac sign options
 */
enum class ZodiacSign {
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
    PISCES
}