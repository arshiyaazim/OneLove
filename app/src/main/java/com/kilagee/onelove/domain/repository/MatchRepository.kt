package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Match
import com.kilagee.onelove.data.model.MatchRequest
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for match operations
 */
interface MatchRepository {
    /**
     * Get all matches for the current user
     * @param limit Maximum number of matches to return
     * @param offset Pagination offset
     * @return Flow of Result containing a list of matches or an error
     */
    fun getMatches(limit: Int = 20, offset: Int = 0): Flow<Result<List<Match>>>
    
    /**
     * Get a specific match by ID
     * @param matchId ID of the match to get
     * @return Flow of Result containing the match or an error
     */
    fun getMatchById(matchId: String): Flow<Result<Match?>>
    
    /**
     * Get match requests received by the current user
     * @param limit Maximum number of requests to return
     * @param offset Pagination offset
     * @return Flow of Result containing a list of match requests or an error
     */
    fun getReceivedMatchRequests(limit: Int = 20, offset: Int = 0): Flow<Result<List<MatchRequest>>>
    
    /**
     * Get match requests sent by the current user
     * @param limit Maximum number of requests to return
     * @param offset Pagination offset
     * @return Flow of Result containing a list of match requests or an error
     */
    fun getSentMatchRequests(limit: Int = 20, offset: Int = 0): Flow<Result<List<MatchRequest>>>
    
    /**
     * Like a user (create a match request)
     * @param userId ID of the user to like
     * @param message Optional message to include with the like
     * @return Result containing the created match request or a match if mutual
     */
    suspend fun likeUser(userId: String, message: String? = null): Result<Any>
    
    /**
     * Dislike a user
     * @param userId ID of the user to dislike
     * @return Result indicating success or failure
     */
    suspend fun dislikeUser(userId: String): Result<Unit>
    
    /**
     * Accept a match request
     * @param requestId ID of the match request to accept
     * @return Result containing the created match or an error
     */
    suspend fun acceptMatchRequest(requestId: String): Result<Match>
    
    /**
     * Decline a match request
     * @param requestId ID of the match request to decline
     * @return Result indicating success or failure
     */
    suspend fun declineMatchRequest(requestId: String): Result<Unit>
    
    /**
     * Unmatch with a user
     * @param matchId ID of the match to delete
     * @return Result indicating success or failure
     */
    suspend fun unmatch(matchId: String): Result<Unit>
    
    /**
     * Get potential matches (discovery)
     * @param limit Maximum number of potential matches to return
     * @return Flow of Result containing a list of users or an error
     */
    fun getPotentialMatches(limit: Int = 20): Flow<Result<List<User>>>
    
    /**
     * Get the match count
     * @return Flow of Result containing the match count or an error
     */
    fun getMatchCount(): Flow<Result<Int>>
    
    /**
     * Get users who have liked the current user but aren't matched yet
     * @param limit Maximum number of users to return
     * @param offset Pagination offset
     * @return Flow of Result containing a list of users or an error
     */
    fun getLikes(limit: Int = 20, offset: Int = 0): Flow<Result<List<User>>>
    
    /**
     * Get number of users who have liked the current user
     * @return Flow of Result containing the like count or an error
     */
    fun getLikeCount(): Flow<Result<Int>>
}