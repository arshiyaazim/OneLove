package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing AI personality type
 */
enum class AIPersonalityType {
    ROMANTIC, FRIENDLY, ADVENTUROUS, INTELLECTUAL, MYSTERIOUS, PLAYFUL, SERIOUS, CREATIVE, CUSTOM
}

/**
 * Enum representing AI availability
 */
enum class AIAvailability {
    FREE, PREMIUM_ONLY, SPECIAL_EVENT, NOT_AVAILABLE
}

/**
 * AI Profile data class for Firestore mapping
 */
data class AIProfile(
    @DocumentId
    val id: String = "",
    
    // Basic info
    val name: String = "",
    val displayName: String = "",
    val bio: String = "",
    val age: Int = 25,
    val gender: UserGender = UserGender.PREFER_NOT_TO_SAY,
    val personalityType: AIPersonalityType = AIPersonalityType.FRIENDLY,
    val interests: List<String> = emptyList(),
    val location: GeoLocation? = null,
    
    // Media
    val avatarUrl: String = "",
    val photos: List<String> = emptyList(),
    val voiceUrl: String? = null,
    
    // Personality traits
    val traits: Map<String, Double> = emptyMap(),
    val personalityDescription: String = "",
    val backstory: String = "",
    
    // Configuration
    val availability: AIAvailability = AIAvailability.FREE,
    val pointsCost: Int = 0,
    val isActive: Boolean = true,
    val maxMessagesPerDay: Int? = null,
    val responseDelay: Int = 0, // seconds
    
    // Response pattern
    val conversationStarters: List<String> = emptyList(),
    val responsePatterns: Map<String, List<String>> = emptyMap(),
    val responsePrompts: Map<String, String> = emptyMap(),
    val modelConfig: Map<String, Any> = emptyMap(),
    
    // Stats
    val popularity: Int = 0,
    val chatsStarted: Int = 0,
    val messagesSent: Int = 0,
    val averageRating: Double = 0.0,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    
    // Additional data
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if profile is premium
     */
    fun isPremium(): Boolean {
        return availability == AIAvailability.PREMIUM_ONLY || pointsCost > 0
    }
    
    /**
     * Get a random conversation starter
     */
    fun getRandomConversationStarter(): String? {
        if (conversationStarters.isEmpty()) return null
        return conversationStarters.random()
    }
    
    /**
     * Get formatted personality type
     */
    fun getFormattedPersonalityType(): String {
        return when (personalityType) {
            AIPersonalityType.ROMANTIC -> "Romantic"
            AIPersonalityType.FRIENDLY -> "Friendly"
            AIPersonalityType.ADVENTUROUS -> "Adventurous"
            AIPersonalityType.INTELLECTUAL -> "Intellectual"
            AIPersonalityType.MYSTERIOUS -> "Mysterious"
            AIPersonalityType.PLAYFUL -> "Playful"
            AIPersonalityType.SERIOUS -> "Serious"
            AIPersonalityType.CREATIVE -> "Creative"
            AIPersonalityType.CUSTOM -> "Custom"
        }
    }
    
    /**
     * Get formatted availability
     */
    fun getFormattedAvailability(): String {
        return when (availability) {
            AIAvailability.FREE -> "Free"
            AIAvailability.PREMIUM_ONLY -> "Premium Only"
            AIAvailability.SPECIAL_EVENT -> "Special Event"
            AIAvailability.NOT_AVAILABLE -> "Not Available"
        }
    }
    
    /**
     * Get top three interests
     */
    fun getTopInterests(limit: Int = 3): List<String> {
        return interests.take(limit)
    }
    
    /**
     * Get response pattern for a mood
     */
    fun getResponsePattern(mood: String): List<String> {
        return responsePatterns[mood] ?: responsePatterns["default"] ?: emptyList()
    }
    
    /**
     * Get prompt for a message type
     */
    fun getPrompt(messageType: String): String {
        return responsePrompts[messageType] ?: responsePrompts["default"] ?: ""
    }
}