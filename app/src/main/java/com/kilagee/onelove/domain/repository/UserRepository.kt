package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.GeoPoint
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.UserSettings
import com.kilagee.onelove.data.model.VerificationStatus
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.Date

/**
 * Repository interface for user operations
 */
interface UserRepository {
    
    /**
     * Create a new user profile
     */
    suspend fun createUserProfile(user: User): Result<String> // Returns user ID
    
    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: String): Result<User>
    
    /**
     * Get user as a flow for real-time updates
     */
    fun getUserFlow(userId: String): Flow<Result<User>>
    
    /**
     * Update user profile
     */
    suspend fun updateUserProfile(user: User): Result<Unit>
    
    /**
     * Update specific user fields
     */
    suspend fun updateUserFields(userId: String, fields: Map<String, Any?>): Result<Unit>
    
    /**
     * Upload profile photo
     */
    suspend fun uploadProfilePhoto(userId: String, photoFile: File): Result<String> // Returns photo URL
    
    /**
     * Delete profile photo
     */
    suspend fun deleteProfilePhoto(userId: String, photoUrl: String): Result<Unit>
    
    /**
     * Update user location
     */
    suspend fun updateUserLocation(userId: String, location: GeoPoint): Result<Unit>
    
    /**
     * Update user settings
     */
    suspend fun updateUserSettings(userId: String, settings: UserSettings): Result<Unit>
    
    /**
     * Update user's last active timestamp
     */
    suspend fun updateLastActive(userId: String, timestamp: Date = Date()): Result<Unit>
    
    /**
     * Block a user
     */
    suspend fun blockUser(currentUserId: String, userToBlockId: String): Result<Unit>
    
    /**
     * Unblock a user
     */
    suspend fun unblockUser(currentUserId: String, userToUnblockId: String): Result<Unit>
    
    /**
     * Get all blocked users
     */
    suspend fun getBlockedUsers(userId: String): Result<List<User>>
    
    /**
     * Request profile verification
     */
    suspend fun requestVerification(userId: String, selfieFile: File, idFile: File): Result<Unit>
    
    /**
     * Get verification status
     */
    suspend fun getVerificationStatus(userId: String): Result<VerificationStatus>
    
    /**
     * Get potential matches for a user based on preferences
     */
    suspend fun getPotentialMatches(userId: String, limit: Int = 20): Result<List<User>>
    
    /**
     * Observe user points
     */
    fun observeUserPoints(userId: String): Flow<Result<Int>>
    
    /**
     * Check if a user is an admin
     */
    suspend fun isUserAdmin(userId: String): Result<Boolean>
    
    /**
     * Search users by name or email (admin only)
     */
    suspend fun searchUsers(query: String, isAdminSearch: Boolean = false): Result<List<User>>
}