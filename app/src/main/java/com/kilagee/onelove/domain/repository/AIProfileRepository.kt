package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.domain.model.AIProfile
import com.kilagee.onelove.domain.model.Message
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for AI Profile operations
 */
interface AIProfileRepository {
    
    /**
     * Get all AI profiles
     */
    suspend fun getAllAIProfiles(includeInactive: Boolean = false): Flow<Result<List<AIProfile>>>
    
    /**
     * Get AI profiles filtered by category
     */
    suspend fun getAIProfilesByCategory(category: String): Flow<Result<List<AIProfile>>>
    
    /**
     * Get an AI profile by ID
     */
    suspend fun getAIProfileById(profileId: String): Flow<Result<AIProfile>>
    
    /**
     * Get recommended AI profiles for a user
     */
    suspend fun getRecommendedAIProfiles(userId: String, limit: Int = 5): Flow<Result<List<AIProfile>>>
    
    /**
     * Send a message to an AI profile and get a response
     */
    suspend fun sendMessageToAIProfile(
        profileId: String, 
        message: String
    ): Flow<Result<Message>>
    
    /**
     * Get conversation history with an AI profile
     */
    suspend fun getAIConversationHistory(
        profileId: String,
        limit: Int = 50,
        startAfter: String? = null
    ): Flow<Result<List<Message>>>
    
    /**
     * Rate an AI profile
     */
    suspend fun rateAIProfile(profileId: String, rating: Double): Flow<Result<Boolean>>
    
    /**
     * Check if a user can access premium AI profiles
     */
    suspend fun canAccessPremiumAIProfiles(userId: String): Flow<Result<Boolean>>
    
    /**
     * Clear conversation history with an AI profile
     */
    suspend fun clearAIConversationHistory(profileId: String): Flow<Result<Boolean>>
}