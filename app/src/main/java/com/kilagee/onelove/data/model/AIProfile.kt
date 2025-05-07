package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

/**
 * AI Profile data model
 * Represents an AI-driven profile that users can interact with
 */
@Parcelize
data class AIProfile(
    @DocumentId val id: String = "",
    val name: String = "",
    val gender: String = "",
    val age: Int = 25,
    val photos: List<String> = emptyList(),
    val bio: String = "",
    val occupation: String = "",
    val education: String = "",
    val interests: List<String> = emptyList(),
    val personality: String = "",
    val personalityTags: List<String> = emptyList(),
    val scriptedResponses: Map<String, List<String>> = emptyMap(),
    val availableResponses: List<ContextualResponse> = emptyList(),
    val premiumOnly: Boolean = false,
    val rating: Double = 5.0,
    val popularityScore: Int = 0,
    val category: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val isActive: Boolean = true
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val PERSONALITY_FRIENDLY = "FRIENDLY"
        const val PERSONALITY_ROMANTIC = "ROMANTIC"
        const val PERSONALITY_INTELLECTUAL = "INTELLECTUAL"
        const val PERSONALITY_ADVENTUROUS = "ADVENTUROUS"
        const val PERSONALITY_HUMOROUS = "HUMOROUS"
        const val PERSONALITY_MYSTERIOUS = "MYSTERIOUS"
        
        const val CATEGORY_POPULAR = "POPULAR"
        const val CATEGORY_NEW = "NEW"
        const val CATEGORY_PREMIUM = "PREMIUM"
    }
}

/**
 * Contextual Response model for AI profiles
 * Defines the AI's response patterns based on context and user input
 */
@Parcelize
data class ContextualResponse(
    val context: String = "",
    val keywords: List<String> = emptyList(),
    val responses: List<String> = emptyList(),
    val probability: Double = 1.0
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(context = "")
}

/**
 * AI Profile Interaction model
 * Records interactions between users and AI profiles
 */
@Parcelize
data class AIProfileInteraction(
    @DocumentId val id: String = "",
    val userId: String = "",
    val aiProfileId: String = "",
    val lastInteractionTime: Timestamp? = null,
    val interactionCount: Int = 0,
    val favorited: Boolean = false,
    val rating: Int = 0,
    val unlocked: Boolean = false
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
}