package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Match
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for match operations
 */
interface MatchRepository {
    
    /**
     * Like a user
     */
    suspend fun likeUser(currentUserId: String, likedUserId: String): Result<Match?> // Returns Match if mutual, null otherwise
    
    /**
     * Dislike a user
     */
    suspend fun dislikeUser(currentUserId: String, dislikedUserId: String): Result<Unit>
    
    /**
     * Super like a user
     */
    suspend fun superLikeUser(currentUserId: String, likedUserId: String): Result<Match?> // Returns Match if mutual, null otherwise
    
    /**
     * Get all matches for a user
     */
    suspend fun getMatches(userId: String): Result<List<Match>>
    
    /**
     * Get all matches for a user as a flow for real-time updates
     */
    fun getMatchesFlow(userId: String): Flow<Result<List<Match>>>
    
    /**
     * Get a specific match by ID
     */
    suspend fun getMatchById(matchId: String): Result<Match>
    
    /**
     * Get a match between two specific users
     */
    suspend fun getMatchBetweenUsers(userId1: String, userId2: String): Result<Match?>
    
    /**
     * Get match as a flow for real-time updates
     */
    fun getMatchFlow(matchId: String): Flow<Result<Match>>
    
    /**
     * Get users who liked the current user
     */
    suspend fun getUsersWhoLikedMe(userId: String): Result<List<User>>
    
    /**
     * Unmatch/remove a match
     */
    suspend fun unmatch(matchId: String, initiatorUserId: String): Result<Unit>
    
    /**
     * Mark a match as having unread messages
     */
    suspend fun markMatchHasUnreadMessage(matchId: String, hasUnread: Boolean = true): Result<Unit>
    
    /**
     * Update the last message info in a match
     */
    suspend fun updateMatchLastMessage(
        matchId: String,
        senderId: String,
        preview: String
    ): Result<Unit>
    
    /**
     * Get AI profile matches for a user
     */
    suspend fun getAIMatches(userId: String): Result<List<Match>>
    
    /**
     * Get regular user matches for a user
     */
    suspend fun getRegularMatches(userId: String): Result<List<Match>>
    
    /**
     * Get users who are mutually matched with the given user
     */
    suspend fun getMatchedUsers(userId: String): Result<List<User>>
}