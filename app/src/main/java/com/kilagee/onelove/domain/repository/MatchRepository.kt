package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.matching.MatchEngine
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for matching functionality
 */
interface MatchRepository {
    
    /**
     * Get potential matches for the current user
     * 
     * @param minMatchPercentage Minimum match percentage to include
     * @param limit Maximum number of matches to return
     * @return Flow of Resource containing a list of MatchEngine.MatchResult
     */
    fun getPotentialMatches(
        minMatchPercentage: Int = 50,
        limit: Int = 50
    ): Flow<Resource<List<MatchEngine.MatchResult>>>
    
    /**
     * Like a user
     * 
     * @param userId ID of the user to like
     * @return Flow of Resource indicating success/failure, with boolean indicating if it's a match
     */
    fun likeUser(userId: String): Flow<Resource<Boolean>>
    
    /**
     * Dislike/Reject a user
     * 
     * @param userId ID of the user to reject
     * @return Flow of Resource indicating success/failure
     */
    fun rejectUser(userId: String): Flow<Resource<Unit>>
    
    /**
     * Get all matches for the current user
     * 
     * @return Flow of Resource containing a list of matched users
     */
    fun getMatches(): Flow<Resource<List<User>>>
    
    /**
     * Unmatch with a user
     * 
     * @param userId ID of the user to unmatch with
     * @return Flow of Resource indicating success/failure
     */
    fun unmatchUser(userId: String): Flow<Resource<Unit>>
}