package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.domain.model.AIMessage
import com.kilagee.onelove.domain.model.AIProfile
import com.kilagee.onelove.domain.model.AIResponseType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing AI profiles data
 */
interface AIProfileRepository {
    
    /**
     * Gets all AI profiles
     */
    fun getAllAIProfiles(): Flow<List<AIProfile>>
    
    /**
     * Gets AI profiles matching the given criteria
     */
    fun getAIProfiles(
        gender: String? = null,
        minAge: Int? = null,
        maxAge: Int? = null,
        personality: String? = null,
        limit: Int = 10
    ): Flow<List<AIProfile>>
    
    /**
     * Gets a single AI profile by ID
     */
    fun getAIProfileById(profileId: String): Flow<AIProfile?>
    
    /**
     * Gets featured AI profiles for the home screen
     */
    fun getFeaturedAIProfiles(limit: Int = 5): Flow<List<AIProfile>>
    
    /**
     * Creates a new AI profile
     */
    suspend fun createAIProfile(profile: AIProfile): Result<AIProfile>
    
    /**
     * Updates an existing AI profile
     */
    suspend fun updateAIProfile(profile: AIProfile): Result<AIProfile>
    
    /**
     * Deletes an AI profile
     */
    suspend fun deleteAIProfile(profileId: String): Result<Boolean>
    
    /**
     * Gets all messages in a conversation with an AI profile
     */
    fun getConversationMessages(conversationId: String): Flow<List<AIMessage>>
    
    /**
     * Sends a message to an AI profile and gets a response
     */
    suspend fun sendMessageToAI(
        conversationId: String,
        message: String,
        preferredResponseType: AIResponseType? = null
    ): Result<AIMessage>
    
    /**
     * Marks all messages in a conversation as read
     */
    suspend fun markConversationAsRead(conversationId: String): Result<Boolean>
    
    /**
     * Gets all conversations for a user
     */
    fun getUserAIConversations(userId: String): Flow<List<Pair<AIProfile, AIMessage?>>>
    
    /**
     * Creates initial AI profiles (for preloading)
     */
    suspend fun createInitialAIProfiles(): Result<Int>
}