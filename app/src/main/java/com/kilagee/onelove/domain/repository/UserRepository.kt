package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related operations
 */
interface UserRepository {
    
    /**
     * Get the current user profile
     */
    suspend fun getCurrentUser(): Flow<Result<User>>
    
    /**
     * Get a user by their ID
     */
    suspend fun getUserById(userId: String): Flow<Result<User>>
    
    /**
     * Search for users based on criteria
     */
    suspend fun searchUsers(criteria: Map<String, Any>): Flow<Result<List<User>>>
    
    /**
     * Get all users that match the current user's preferences
     */
    suspend fun getPotentialMatches(limit: Int = 50): Flow<Result<List<User>>>
    
    /**
     * Update the current user's profile
     */
    suspend fun updateUserProfile(user: User): Flow<Result<Boolean>>
    
    /**
     * Upload a profile photo
     */
    suspend fun uploadProfilePhoto(photoUri: String): Flow<Result<String>>
    
    /**
     * Set the user's online status
     */
    suspend fun setUserOnlineStatus(isOnline: Boolean): Flow<Result<Boolean>>
    
    /**
     * Block a user
     */
    suspend fun blockUser(userId: String): Flow<Result<Boolean>>
    
    /**
     * Unblock a user
     */
    suspend fun unblockUser(userId: String): Flow<Result<Boolean>>
    
    /**
     * Report a user
     */
    suspend fun reportUser(userId: String, reason: String): Flow<Result<Boolean>>
    
    /**
     * Get blocked users
     */
    suspend fun getBlockedUsers(): Flow<Result<List<User>>>
    
    /**
     * Check if the current user has verified their profile
     */
    suspend fun isProfileVerified(): Flow<Result<Boolean>>
    
    /**
     * Submit a verification request
     */
    suspend fun submitVerification(idPhotoUri: String, selfieUri: String): Flow<Result<Boolean>>
}