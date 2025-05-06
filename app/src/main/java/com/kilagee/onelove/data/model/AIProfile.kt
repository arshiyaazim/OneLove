package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Enum representing AI profile personality types
 */
enum class AIPersonalityType {
    FRIENDLY, ROMANTIC, FLIRTY, PROFESSIONAL, MOTIVATIONAL, HELPFUL, ADVENTUROUS, MYSTERIOUS, ARTISTIC, INTELLECTUAL
}

/**
 * Enum representing AI profile language proficiency levels
 */
enum class LanguageProficiency {
    BASIC, INTERMEDIATE, FLUENT, NATIVE
}

/**
 * Enum representing AI profile availability status
 */
enum class AIAvailabilityStatus {
    AVAILABLE, BUSY, AWAY, OFFLINE
}

/**
 * Enum representing AI response types
 */
enum class AIResponseType {
    TEXT, TEXT_WITH_EMOJI, TEXT_WITH_IMAGE, VOICE, SHORT, LONG, QUESTION, STATEMENT, CUSTOM
}

/**
 * Sealed class representing AI scripted behaviors
 */
sealed class AIBehavior {
    data class GreetingBehavior(
        val greetings: List<String>,
        val timeBasedGreetings: Map<String, List<String>> = emptyMap(), // morning, afternoon, evening, night
        val personalizedGreetings: List<String> = emptyList()
    ) : AIBehavior()
    
    data class InterestBasedResponse(
        val interests: Map<String, List<String>> // interest category to response options
    ) : AIBehavior()
    
    data class ConversationStarter(
        val topics: List<String>,
        val questions: List<String>
    ) : AIBehavior()
    
    data class EmotionalResponse(
        val emotions: Map<String, List<String>> // emotion to response options
    ) : AIBehavior()
    
    data class StorytellingBehavior(
        val stories: List<String>,
        val introductions: List<String>,
        val conclusions: List<String>
    ) : AIBehavior()
}

/**
 * Data class representing an AI Profile for scripted interactions
 */
data class AIProfile(
    @DocumentId
    val id: String = "",
    
    // Basic info
    val name: String = "",
    val gender: UserGender = UserGender.FEMALE,
    val age: Int = 25,
    val bio: String = "",
    val description: String = "",
    val personality: AIPersonalityType = AIPersonalityType.FRIENDLY,
    val interests: List<String> = emptyList(),
    val traits: List<String> = emptyList(),
    val occupation: String = "",
    val background: String = "",
    
    // Media assets
    val profileImageUrl: String = "",
    val galleryImages: List<String> = emptyList(),
    val voiceUrl: String? = null,
    
    // Interaction settings
    val availabilityStatus: AIAvailabilityStatus = AIAvailabilityStatus.AVAILABLE,
    val responseDelay: IntRange = IntRange(2, 5), // Response delay range in seconds
    val typingSpeedWpm: Int = 60, // Words per minute typing speed
    val responseLength: IntRange = IntRange(10, 50), // Response length range in words
    val emojiFrequency: Double = 0.3, // Probability of using emojis (0-1)
    val preferredResponseTypes: List<AIResponseType> = listOf(AIResponseType.TEXT),
    val messageInitiationRate: Double = 0.2, // Probability of initiating new conversation (0-1)
    val questionFrequency: Double = 0.4, // Probability of asking questions (0-1)
    
    // Language capabilities
    val primaryLanguage: String = "en",
    val supportedLanguages: Map<String, LanguageProficiency> = mapOf("en" to LanguageProficiency.NATIVE),
    
    // Scripted behaviors
    @get:PropertyName("behaviors")
    @set:PropertyName("behaviors")
    var behaviorsMap: Map<String, Map<String, Any>> = emptyMap(), // Serialized AIBehavior classes
    
    // Response templates
    val greetings: List<String> = emptyList(),
    val farewells: List<String> = emptyList(),
    val questions: List<String> = emptyList(),
    val responses: Map<String, List<String>> = emptyMap(), // Topic to responses mapping
    val icebreakers: List<String> = emptyList(),
    
    // Conversation memory
    val memoryCapacity: Int = 10, // Number of past messages to remember
    val knownUsers: List<String> = emptyList(),
    val conversationTopics: Map<String, Double> = emptyMap(), // Topic to frequency mapping
    
    // Stats and usage
    val popularity: Int = 0, // Popularity score (0-100)
    val messageCount: Int = 0,
    val activeChats: Int = 0,
    val userRating: Double = 5.0, // Average user rating (1-5)
    val premiumOnly: Boolean = false,
    val isActive: Boolean = true,
    val dailyMessageLimit: Int = 100,
    
    // Administrative
    val creatorId: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    val tags: List<String> = emptyList(),
    val category: String = "",
    val version: String = "1.0",
    
    // Custom fields
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Retrieve typed behaviors from serialized map
     */
    @Exclude
    fun getBehaviors(): List<AIBehavior> {
        val behaviors = mutableListOf<AIBehavior>()
        
        behaviorsMap["greeting"]?.let { map ->
            val greetings = map["greetings"] as? List<String> ?: emptyList()
            val timeBasedMap = map["timeBasedGreetings"] as? Map<String, List<String>> ?: emptyMap()
            val personalized = map["personalizedGreetings"] as? List<String> ?: emptyList()
            
            behaviors.add(
                AIBehavior.GreetingBehavior(
                    greetings = greetings,
                    timeBasedGreetings = timeBasedMap,
                    personalizedGreetings = personalized
                )
            )
        }
        
        behaviorsMap["interestBased"]?.let { map ->
            val interestsMap = map["interests"] as? Map<String, List<String>> ?: emptyMap()
            behaviors.add(AIBehavior.InterestBasedResponse(interestsMap))
        }
        
        behaviorsMap["conversationStarter"]?.let { map ->
            val topics = map["topics"] as? List<String> ?: emptyList()
            val questions = map["questions"] as? List<String> ?: emptyList()
            behaviors.add(AIBehavior.ConversationStarter(topics, questions))
        }
        
        behaviorsMap["emotionalResponse"]?.let { map ->
            val emotionsMap = map["emotions"] as? Map<String, List<String>> ?: emptyMap()
            behaviors.add(AIBehavior.EmotionalResponse(emotionsMap))
        }
        
        behaviorsMap["storytelling"]?.let { map ->
            val stories = map["stories"] as? List<String> ?: emptyList()
            val intros = map["introductions"] as? List<String> ?: emptyList()
            val conclusions = map["conclusions"] as? List<String> ?: emptyList()
            behaviors.add(AIBehavior.StorytellingBehavior(stories, intros, conclusions))
        }
        
        return behaviors
    }
    
    /**
     * Serialize behaviors to map for storage
     */
    @Exclude
    fun serializeBehaviors(behaviors: List<AIBehavior>): Map<String, Map<String, Any>> {
        val result = mutableMapOf<String, Map<String, Any>>()
        
        behaviors.forEach { behavior ->
            when (behavior) {
                is AIBehavior.GreetingBehavior -> {
                    result["greeting"] = mapOf(
                        "greetings" to behavior.greetings,
                        "timeBasedGreetings" to behavior.timeBasedGreetings,
                        "personalizedGreetings" to behavior.personalizedGreetings
                    )
                }
                
                is AIBehavior.InterestBasedResponse -> {
                    result["interestBased"] = mapOf(
                        "interests" to behavior.interests
                    )
                }
                
                is AIBehavior.ConversationStarter -> {
                    result["conversationStarter"] = mapOf(
                        "topics" to behavior.topics,
                        "questions" to behavior.questions
                    )
                }
                
                is AIBehavior.EmotionalResponse -> {
                    result["emotionalResponse"] = mapOf(
                        "emotions" to behavior.emotions
                    )
                }
                
                is AIBehavior.StorytellingBehavior -> {
                    result["storytelling"] = mapOf(
                        "stories" to behavior.stories,
                        "introductions" to behavior.introductions,
                        "conclusions" to behavior.conclusions
                    )
                }
            }
        }
        
        return result
    }
    
    /**
     * Get greeting based on time of day
     */
    @Exclude
    fun getTimeBasedGreeting(): String {
        val behaviors = getBehaviors()
        val greetingBehavior = behaviors.filterIsInstance<AIBehavior.GreetingBehavior>().firstOrNull()
        
        if (greetingBehavior != null) {
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            val timeOfDay = when {
                hour < 12 -> "morning"
                hour < 17 -> "afternoon"
                hour < 21 -> "evening"
                else -> "night"
            }
            
            val timeBasedGreetings = greetingBehavior.timeBasedGreetings[timeOfDay]
            if (!timeBasedGreetings.isNullOrEmpty()) {
                return timeBasedGreetings.random()
            }
            
            // Fallback to general greetings
            if (greetingBehavior.greetings.isNotEmpty()) {
                return greetingBehavior.greetings.random()
            }
        }
        
        // Fallback to default greeting
        return if (greetings.isNotEmpty()) greetings.random() else "Hello!"
    }
    
    /**
     * Get response for a specific topic
     */
    @Exclude
    fun getResponseForTopic(topic: String): String? {
        // First check scripted behaviors
        val behaviors = getBehaviors()
        val interestBehavior = behaviors.filterIsInstance<AIBehavior.InterestBasedResponse>().firstOrNull()
        
        if (interestBehavior != null) {
            for ((interest, responses) in interestBehavior.interests) {
                if (topic.contains(interest, ignoreCase = true) && responses.isNotEmpty()) {
                    return responses.random()
                }
            }
        }
        
        // Then check direct response mapping
        for ((responseTopic, responseList) in responses) {
            if (topic.contains(responseTopic, ignoreCase = true) && responseList.isNotEmpty()) {
                return responseList.random()
            }
        }
        
        return null
    }
    
    /**
     * Get a random question to ask
     */
    @Exclude
    fun getRandomQuestion(): String? {
        // First check scripted behaviors
        val behaviors = getBehaviors()
        val conversationStarter = behaviors.filterIsInstance<AIBehavior.ConversationStarter>().firstOrNull()
        
        if (conversationStarter != null && conversationStarter.questions.isNotEmpty()) {
            return conversationStarter.questions.random()
        }
        
        // Fallback to simple questions list
        return if (questions.isNotEmpty()) questions.random() else null
    }
    
    /**
     * Get a random icebreaker
     */
    @Exclude
    fun getRandomIcebreaker(): String? {
        return if (icebreakers.isNotEmpty()) icebreakers.random() else null
    }
    
    /**
     * Get a simulated typing delay based on message length
     */
    @Exclude
    fun getTypingDelayForMessage(messageLength: Int): Long {
        // Calculate typing time based on typing speed (WPM)
        // Assuming average word length of 5 characters
        val words = messageLength / 5.0
        val minutesToType = words / typingSpeedWpm
        val secondsToType = (minutesToType * 60).toLong()
        
        // Add random variation (±20%)
        val variationFactor = 0.8 + Math.random() * 0.4
        
        // Ensure minimum delay from responseDelay range
        return (secondsToType * 1000 * variationFactor).toLong().coerceAtLeast(responseDelay.first * 1000L)
    }
    
    /**
     * Check if this profile supports a specific language
     */
    @Exclude
    fun supportsLanguage(languageCode: String): Boolean {
        return supportedLanguages.containsKey(languageCode)
    }
    
    /**
     * Check if profile is premium only
     */
    @Exclude
    fun isPremiumOnly(): Boolean {
        return premiumOnly
    }
}