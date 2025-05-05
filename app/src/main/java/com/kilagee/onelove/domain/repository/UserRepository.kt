package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.domain.model.GeoPoint
import com.kilagee.onelove.domain.model.Resource
import com.kilagee.onelove.domain.model.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related operations
 */
interface UserRepository {
    
    /**
     * Get current user profile
     */
    fun getCurrentUser(): Flow<Resource<User>>
    
    /**
     * Get user by ID
     */
    fun getUserById(userId: String): Deferred<Resource<User>>
    
    /**
     * Get users by IDs
     */
    fun getUsersByIds(userIds: List<String>): Flow<Resource<List<User>>>
    
    /**
     * Get users by location proximity
     */
    fun getUsersByLocation(center: GeoPoint, radiusKm: Double): Flow<Resource<List<User>>>
    
    /**
     * Get users by interests
     */
    fun getUsersByInterests(interests: List<String>): Flow<Resource<List<User>>>
    
    /**
     * Update current user profile
     */
    fun updateUserProfile(user: User): Flow<Resource<User>>
    
    /**
     * Update user location
     */
    fun updateUserLocation(location: GeoPoint): Flow<Resource<Unit>>
    
    /**
     * Update user online status
     */
    fun updateOnlineStatus(isOnline: Boolean): Flow<Resource<Unit>>
    
    /**
     * Upload profile photo
     */
    fun uploadProfilePhoto(photoUri: String): Flow<Resource<String>>
    
    /**
     * Get verified users
     */
    fun getVerifiedUsers(): Flow<Resource<List<User>>>
    
    /**
     * Get premium users
     */
    fun getPremiumUsers(): Flow<Resource<List<User>>>
    
    /**
     * Search users by name or other criteria
     */
    fun searchUsers(query: String): Flow<Resource<List<User>>>
    
    /**
     * Block a user
     */
    fun blockUser(userId: String): Flow<Resource<Unit>>
    
    /**
     * Get blocked users
     */
    fun getBlockedUsers(): Flow<Resource<List<User>>>
    
    /**
     * Unblock a user
     */
    fun unblockUser(userId: String): Flow<Resource<Unit>>
}