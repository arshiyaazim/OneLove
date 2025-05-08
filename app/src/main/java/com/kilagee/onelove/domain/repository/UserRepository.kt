package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.util.Result

/**
 * Interface for user-related operations
 */
interface UserRepository {
    
    /**
     * Get users by IDs
     * @param userIds List of user IDs to retrieve
     * @return List of [User] objects
     */
    suspend fun getUsersById(userIds: List<String>): Result<List<User>>
    
    /**
     * Get recent chat partners
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun getRecentChatPartners(limit: Int = 10): Result<List<User>>
    
    /**
     * Get user matches
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun getMatches(limit: Int = 50): Result<List<User>>
    
    /**
     * Get users liked by the current user
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun getLikedUsers(limit: Int = 50): Result<List<User>>
    
    /**
     * Get profiles visited by the current user
     * @param limit Maximum number of profiles to retrieve
     * @return List of [User] objects
     */
    suspend fun getVisitedProfiles(limit: Int = 50): Result<List<User>>
    
    /**
     * Get users who liked the current user
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun getUsersWhoLikedMe(limit: Int = 50): Result<List<User>>
    
    /**
     * Get blocked users
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun getBlockedUsers(limit: Int = 50): Result<List<User>>
    
    /**
     * Block a user
     * @param userId ID of the user to block
     */
    suspend fun blockUser(userId: String): Result<Unit>
    
    /**
     * Unblock a user
     * @param userId ID of the user to unblock
     */
    suspend fun unblockUser(userId: String): Result<Unit>
    
    /**
     * Search users by criteria
     * @param query Search query
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun searchUsers(query: String, limit: Int = 20): Result<List<User>>
    
    /**
     * Track a profile visit
     * @param userId ID of the user whose profile was visited
     */
    suspend fun trackProfileVisit(userId: String): Result<Unit>
    
    /**
     * Get recommended users based on preferences and interactions
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun getRecommendedUsers(limit: Int = 20): Result<List<User>>
    
    /**
     * Get popular users within the app
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun getPopularUsers(limit: Int = 20): Result<List<User>>
    
    /**
     * Get nearby users based on current location
     * @param radiusKm Radius in kilometers
     * @param limit Maximum number of users to retrieve
     * @return List of [User] objects
     */
    suspend fun getNearbyUsers(radiusKm: Int = 50, limit: Int = 20): Result<List<User>>
}