package com.kilagee.onelove.data.repository

import com.kilagee.onelove.data.model.Match
import com.kilagee.onelove.data.model.MatchStatus
import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.UserMatchAction
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for matches
 */
interface MatchRepository {
    
    /**
     * Get match by ID
     * @param matchId Match ID
     * @return Result containing the match or error
     */
    suspend fun getMatchById(matchId: String): Result<Match>
    
    /**
     * Get match by ID as Flow
     * @param matchId Match ID
     * @return Flow emitting Result containing the match or error
     */
    fun getMatchByIdFlow(matchId: String): Flow<Result<Match>>
    
    /**
     * Get match between users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Result containing the match or error
     */
    suspend fun getMatchBetweenUsers(userId1: String, userId2: String): Result<Match?>
    
    /**
     * Create potential match
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @param matchScore Match score
     * @return Result containing the match ID or error
     */
    suspend fun createPotentialMatch(userId1: String, userId2: String, matchScore: Int): Result<String>
    
    /**
     * Get user matches
     * @param userId User ID
     * @param status Optional filter by match status
     * @param limit Maximum number of results
     * @return Result containing list of matches or error
     */
    suspend fun getUserMatches(
        userId: String,
        status: MatchStatus? = null,
        limit: Int = 50
    ): Result<List<Match>>
    
    /**
     * Get user matches as Flow
     * @param userId User ID
     * @param status Optional filter by match status
     * @return Flow emitting Result containing list of matches or error
     */
    fun getUserMatchesFlow(
        userId: String,
        status: MatchStatus? = null
    ): Flow<Result<List<Match>>>
    
    /**
     * Get matched users
     * @param userId User ID
     * @param limit Maximum number of results
     * @return Result containing list of matched users or error
     */
    suspend fun getMatchedUsers(userId: String, limit: Int = 50): Result<List<User>>
    
    /**
     * Get matched users as Flow
     * @param userId User ID
     * @return Flow emitting Result containing list of matched users or error
     */
    fun getMatchedUsersFlow(userId: String): Flow<Result<List<User>>>
    
    /**
     * Get pending matches
     * @param userId User ID
     * @param limit Maximum number of results
     * @return Result containing list of pending matches or error
     */
    suspend fun getPendingMatches(userId: String, limit: Int = 50): Result<List<Match>>
    
    /**
     * Get potential matches
     * @param userId User ID
     * @param limit Maximum number of results
     * @return Result containing list of users who could be matches or error
     */
    suspend fun getPotentialMatches(userId: String, limit: Int = 50): Result<List<User>>
    
    /**
     * Update user action on match
     * @param matchId Match ID
     * @param userId User ID
     * @param action User action
     * @return Result containing the updated match or error
     */
    suspend fun updateUserMatchAction(
        matchId: String,
        userId: String,
        action: UserMatchAction
    ): Result<Match>
    
    /**
     * Like user
     * @param currentUserId Current user ID
     * @param likedUserId Liked user ID
     * @param isSuperLike Whether it's a super like
     * @return Result containing the match (if created) or error
     */
    suspend fun likeUser(
        currentUserId: String,
        likedUserId: String,
        isSuperLike: Boolean = false
    ): Result<Match>
    
    /**
     * Pass on user (decline match)
     * @param currentUserId Current user ID
     * @param passedUserId Passed user ID
     * @return Result indicating success or error
     */
    suspend fun passUser(currentUserId: String, passedUserId: String): Result<Unit>
    
    /**
     * Unmatch users
     * @param matchId Match ID
     * @param reason Optional reason for unmatching
     * @return Result indicating success or error
     */
    suspend fun unmatch(matchId: String, reason: String? = null): Result<Unit>
    
    /**
     * Get user's favorite matches
     * @param userId User ID
     * @return Result containing list of favorite matches or error
     */
    suspend fun getFavoriteMatches(userId: String): Result<List<Match>>
    
    /**
     * Add match to favorites
     * @param matchId Match ID
     * @param userId User ID
     * @return Result indicating success or error
     */
    suspend fun addMatchToFavorites(matchId: String, userId: String): Result<Unit>
    
    /**
     * Remove match from favorites
     * @param matchId Match ID
     * @param userId User ID
     * @return Result indicating success or error
     */
    suspend fun removeMatchFromFavorites(matchId: String, userId: String): Result<Unit>
    
    /**
     * Get match statistics
     * @param userId User ID
     * @return Result containing map of statistics or error
     */
    suspend fun getMatchStatistics(userId: String): Result<Map<String, Int>>
    
    /**
     * Get newly matched users
     * @param userId User ID
     * @param limit Maximum number of results
     * @return Result containing list of newly matched users or error
     */
    suspend fun getNewMatches(userId: String, limit: Int = 10): Result<List<User>>
}