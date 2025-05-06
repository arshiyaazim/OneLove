package com.kilagee.onelove.data.repository

import android.net.Uri
import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.UserGender
import com.kilagee.onelove.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for user management
 */
interface UserRepository {
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return Result containing the user or error
     */
    suspend fun getUserById(userId: String): Result<User>
    
    /**
     * Get user by ID as Flow
     * @param userId User ID
     * @return Flow emitting Result containing the user or error
     */
    fun getUserByIdFlow(userId: String): Flow<Result<User>>
    
    /**
     * Create new user profile
     * @param user User data
     * @return Result containing the created user ID or error
     */
    suspend fun createUser(user: User): Result<String>
    
    /**
     * Update user profile
     * @param user User data
     * @return Result containing the updated user or error
     */
    suspend fun updateUser(user: User): Result<User>
    
    /**
     * Update user profile picture
     * @param userId User ID
     * @param imageUri Image URI
     * @return Result containing the image URL or error
     */
    suspend fun updateProfilePicture(userId: String, imageUri: Uri): Result<String>
    
    /**
     * Update user cover photo
     * @param userId User ID
     * @param imageUri Image URI
     * @return Result containing the image URL or error
     */
    suspend fun updateCoverPhoto(userId: String, imageUri: Uri): Result<String>
    
    /**
     * Upload user photo to gallery
     * @param userId User ID
     * @param imageUri Image URI
     * @return Result containing the image URL or error
     */
    suspend fun uploadUserPhoto(userId: String, imageUri: Uri): Result<String>
    
    /**
     * Delete user photo from gallery
     * @param userId User ID
     * @param photoUrl Photo URL
     * @return Result indicating success or error
     */
    suspend fun deleteUserPhoto(userId: String, photoUrl: String): Result<Unit>
    
    /**
     * Upload verification documents
     * @param userId User ID
     * @param documentUris List of document URIs
     * @return Result containing the document URLs or error
     */
    suspend fun uploadVerificationDocuments(userId: String, documentUris: List<Uri>): Result<List<String>>
    
    /**
     * Update user verification status
     * @param userId User ID
     * @param status Verification status
     * @return Result indicating success or error
     */
    suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Result<Unit>
    
    /**
     * Update user online status
     * @param userId User ID
     * @param isOnline Online status
     * @return Result indicating success or error
     */
    suspend fun updateOnlineStatus(userId: String, isOnline: Boolean): Result<Unit>
    
    /**
     * Update user last active timestamp
     * @param userId User ID
     * @param lastActive Last active timestamp
     * @return Result indicating success or error
     */
    suspend fun updateLastActive(userId: String, lastActive: Date): Result<Unit>
    
    /**
     * Update user FCM token
     * @param userId User ID
     * @param token FCM token
     * @return Result indicating success or error
     */
    suspend fun updateFcmToken(userId: String, token: String): Result<Unit>
    
    /**
     * Block user
     * @param userId Current user ID
     * @param blockedUserId User ID to block
     * @return Result indicating success or error
     */
    suspend fun blockUser(userId: String, blockedUserId: String): Result<Unit>
    
    /**
     * Unblock user
     * @param userId Current user ID
     * @param unblockedUserId User ID to unblock
     * @return Result indicating success or error
     */
    suspend fun unblockUser(userId: String, unblockedUserId: String): Result<Unit>
    
    /**
     * Get blocked users
     * @param userId User ID
     * @return Result containing list of blocked user IDs or error
     */
    suspend fun getBlockedUsers(userId: String): Result<List<String>>
    
    /**
     * Search users by query
     * @param query Search query
     * @param limit Maximum number of results
     * @return Result containing list of users or error
     */
    suspend fun searchUsers(query: String, limit: Int = 20): Result<List<User>>
    
    /**
     * Get suggested users based on preferences
     * @param userId Current user ID
     * @param limit Maximum number of results
     * @return Result containing list of suggested users or error
     */
    suspend fun getSuggestedUsers(userId: String, limit: Int = 20): Result<List<User>>
    
    /**
     * Update user gender
     * @param userId User ID
     * @param gender Gender
     * @return Result indicating success or error
     */
    suspend fun updateGender(userId: String, gender: UserGender): Result<Unit>
    
    /**
     * Update user gender preferences
     * @param userId User ID
     * @param preferences List of preferred genders
     * @return Result indicating success or error
     */
    suspend fun updateGenderPreferences(userId: String, preferences: List<UserGender>): Result<Unit>
    
    /**
     * Update user interests
     * @param userId User ID
     * @param interests List of interests
     * @return Result indicating success or error
     */
    suspend fun updateInterests(userId: String, interests: List<String>): Result<Unit>
    
    /**
     * Add points to user
     * @param userId User ID
     * @param points Points to add
     * @return Result containing updated point total or error
     */
    suspend fun addPoints(userId: String, points: Int): Result<Int>
    
    /**
     * Subtract points from user
     * @param userId User ID
     * @param points Points to subtract
     * @return Result containing updated point total or error
     */
    suspend fun subtractPoints(userId: String, points: Int): Result<Int>
    
    /**
     * Get all verified users
     * @param limit Maximum number of results
     * @return Result containing list of verified users or error
     */
    suspend fun getVerifiedUsers(limit: Int = 50): Result<List<User>>
    
    /**
     * Get all admin users
     * @return Result containing list of admin users or error
     */
    suspend fun getAdminUsers(): Result<List<User>>
    
    /**
     * Delete user account
     * @param userId User ID
     * @return Result indicating success or error
     */
    suspend fun deleteUserAccount(userId: String): Result<Unit>
}