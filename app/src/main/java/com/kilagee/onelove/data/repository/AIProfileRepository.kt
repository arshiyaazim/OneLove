package com.kilagee.onelove.data.repository

import android.net.Uri
import com.kilagee.onelove.data.model.AIProfile
import com.kilagee.onelove.data.model.AIProfileCategory
import com.kilagee.onelove.data.model.AvailabilityTime
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.UserGender
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for AI profiles
 */
interface AIProfileRepository {
    
    /**
     * Get AI profile by ID
     * @param profileId Profile ID
     * @return Result containing the AI profile or error
     */
    suspend fun getAIProfileById(profileId: String): Result<AIProfile>
    
    /**
     * Get AI profile by ID as Flow
     * @param profileId Profile ID
     * @return Flow emitting Result containing the AI profile or error
     */
    fun getAIProfileByIdFlow(profileId: String): Flow<Result<AIProfile>>
    
    /**
     * Get all AI profiles
     * @param isPremiumUser Whether the requesting user has premium subscription
     * @param limit Maximum number of results
     * @return Result containing list of AI profiles or error
     */
    suspend fun getAllAIProfiles(
        isPremiumUser: Boolean,
        limit: Int = 50
    ): Result<List<AIProfile>>
    
    /**
     * Get all AI profiles as Flow
     * @param isPremiumUser Whether the requesting user has premium subscription
     * @return Flow emitting Result containing list of AI profiles or error
     */
    fun getAllAIProfilesFlow(isPremiumUser: Boolean): Flow<Result<List<AIProfile>>>
    
    /**
     * Get AI profiles by category
     * @param category AI profile category
     * @param isPremiumUser Whether the requesting user has premium subscription
     * @param limit Maximum number of results
     * @return Result containing list of AI profiles or error
     */
    suspend fun getAIProfilesByCategory(
        category: AIProfileCategory,
        isPremiumUser: Boolean,
        limit: Int = 20
    ): Result<List<AIProfile>>
    
    /**
     * Get featured AI profiles
     * @param isPremiumUser Whether the requesting user has premium subscription
     * @param limit Maximum number of results
     * @return Result containing list of AI profiles or error
     */
    suspend fun getFeaturedAIProfiles(
        isPremiumUser: Boolean,
        limit: Int = 10
    ): Result<List<AIProfile>>
    
    /**
     * Create AI profile (admin only)
     * @param name Profile name
     * @param bio Profile bio
     * @param age Age
     * @param gender Gender
     * @param interests List of interests
     * @param avatarUri Avatar image URI
     * @param photoUris List of photo URIs
     * @param voiceUri Optional voice URI
     * @param personalityTraits Personality traits
     * @param responseScripts Map of response scripts
     * @param introMessages Intro messages
     * @param category AI profile category
     * @param availableTimes List of available time periods
     * @param premiumOnly Whether profile is premium-only
     * @param conversationStarters List of conversation starters
     * @return Result containing the created profile ID or error
     */
    suspend fun createAIProfile(
        name: String,
        bio: String,
        age: Int,
        gender: UserGender,
        interests: List<String>,
        avatarUri: Uri,
        photoUris: List<Uri>,
        voiceUri: Uri? = null,
        personalityTraits: List<String>,
        responseScripts: Map<String, List<String>>,
        introMessages: List<String>,
        category: AIProfileCategory,
        availableTimes: List<AvailabilityTime>,
        premiumOnly: Boolean = true,
        conversationStarters: List<String> = emptyList()
    ): Result<String>
    
    /**
     * Update AI profile (admin only)
     * @param profileId Profile ID
     * @param updates Map of profile field updates
     * @return Result containing the updated profile or error
     */
    suspend fun updateAIProfile(
        profileId: String,
        updates: Map<String, Any?>
    ): Result<AIProfile>
    
    /**
     * Delete AI profile (admin only)
     * @param profileId Profile ID
     * @return Result indicating success or error
     */
    suspend fun deleteAIProfile(profileId: String): Result<Unit>
    
    /**
     * Set AI profile featured status (admin only)
     * @param profileId Profile ID
     * @param isFeatured Featured status
     * @return Result containing the updated profile or error
     */
    suspend fun setAIProfileFeaturedStatus(
        profileId: String,
        isFeatured: Boolean
    ): Result<AIProfile>
    
    /**
     * Generate AI response message
     * @param profileId AI profile ID
     * @param userId User ID
     * @param userMessage User message content
     * @param chatId Chat ID
     * @return Result containing the generated message or error
     */
    suspend fun generateAIResponse(
        profileId: String,
        userId: String,
        userMessage: String,
        chatId: String
    ): Result<Message>
    
    /**
     * Get user's recent AI chats
     * @param userId User ID
     * @param limit Maximum number of results
     * @return Result containing map of AI profile IDs to recent messages or error
     */
    suspend fun getUserRecentAIChats(
        userId: String,
        limit: Int = 5
    ): Result<Map<String, List<Message>>>
    
    /**
     * Get AI profile interaction count
     * @param profileId Profile ID
     * @return Result containing the interaction count or error
     */
    suspend fun getAIProfileInteractionCount(profileId: String): Result<Int>
    
    /**
     * Increment AI profile interaction count
     * @param profileId Profile ID
     * @return Result containing the new interaction count or error
     */
    suspend fun incrementAIProfileInteractionCount(profileId: String): Result<Int>
    
    /**
     * Get recommended AI profiles for user
     * @param userId User ID
     * @param isPremiumUser Whether the user has premium subscription
     * @param limit Maximum number of results
     * @return Result containing list of recommended AI profiles or error
     */
    suspend fun getRecommendedAIProfilesForUser(
        userId: String,
        isPremiumUser: Boolean,
        limit: Int = 5
    ): Result<List<AIProfile>>
    
    /**
     * Add new response scripts to AI profile (admin only)
     * @param profileId Profile ID
     * @param triggerPhrase Trigger phrase
     * @param responses List of possible responses
     * @return Result containing the updated profile or error
     */
    suspend fun addAIProfileResponseScripts(
        profileId: String,
        triggerPhrase: String,
        responses: List<String>
    ): Result<AIProfile>
}