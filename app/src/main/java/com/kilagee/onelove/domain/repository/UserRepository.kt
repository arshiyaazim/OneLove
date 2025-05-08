package com.kilagee.onelove.domain.repository

import android.net.Uri
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.VerificationLevel
import com.kilagee.onelove.data.model.VerificationRequest
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user operations
 */
interface UserRepository {
    /**
     * Get a user by ID
     * @param userId ID of the user to get
     * @return Flow of Result containing the user or an error
     */
    fun getUserById(userId: String): Flow<Result<User?>>
    
    /**
     * Get users by their IDs
     * @param userIds List of user IDs to get
     * @return Flow of Result containing a list of users or an error
     */
    fun getUsersByIds(userIds: List<String>): Flow<Result<List<User>>>
    
    /**
     * Get nearby users for discovery
     * @param maxDistance Maximum distance in kilometers
     * @param limit Maximum number of users to return
     * @param minAge Minimum age filter
     * @param maxAge Maximum age filter
     * @param genderPreferences Gender preferences filter
     * @param excludeIds User IDs to exclude from results
     * @return Flow of Result containing a list of users or an error
     */
    fun getNearbyUsers(
        maxDistance: Int,
        limit: Int = 20,
        minAge: Int? = null,
        maxAge: Int? = null,
        genderPreferences: List<String>? = null,
        excludeIds: List<String> = emptyList()
    ): Flow<Result<List<User>>>
    
    /**
     * Search users by name or other attributes
     * @param query Query string for search
     * @param limit Maximum number of users to return
     * @return Flow of Result containing a list of users or an error
     */
    fun searchUsers(query: String, limit: Int = 20): Flow<Result<List<User>>>
    
    /**
     * Update user profile information
     * @param user Updated user data
     * @return Result containing the updated user or an error
     */
    suspend fun updateProfile(user: User): Result<User>
    
    /**
     * Update user's profile photo
     * @param photoUri URI of the photo to upload
     * @return Result containing the updated photo URL or an error
     */
    suspend fun updateProfilePhoto(photoUri: Uri): Result<String>
    
    /**
     * Upload additional photos to the user's profile
     * @param photoUris List of photo URIs to upload
     * @return Result containing a list of uploaded photo URLs or an error
     */
    suspend fun uploadPhotos(photoUris: List<Uri>): Result<List<String>>
    
    /**
     * Delete a photo from the user's profile
     * @param photoUrl URL of the photo to delete
     * @return Result indicating success or failure
     */
    suspend fun deletePhoto(photoUrl: String): Result<Unit>
    
    /**
     * Update user location
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @param locationName Optional name of the location
     * @return Result indicating success or failure
     */
    suspend fun updateLocation(
        latitude: Double,
        longitude: Double,
        locationName: String? = null
    ): Result<Unit>
    
    /**
     * Update user's online status
     * @param isOnline Whether the user is online
     * @return Result indicating success or failure
     */
    suspend fun updateOnlineStatus(isOnline: Boolean): Result<Unit>
    
    /**
     * Block another user
     * @param userId ID of the user to block
     * @return Result indicating success or failure
     */
    suspend fun blockUser(userId: String): Result<Unit>
    
    /**
     * Unblock a previously blocked user
     * @param userId ID of the user to unblock
     * @return Result indicating success or failure
     */
    suspend fun unblockUser(userId: String): Result<Unit>
    
    /**
     * Get a list of blocked users
     * @return Flow of Result containing a list of blocked user IDs or an error
     */
    fun getBlockedUsers(): Flow<Result<List<String>>>
    
    /**
     * Submit a verification request
     * @param request Verification request data
     * @param idPhotoUri URI of the ID photo
     * @param selfieUri URI of the selfie photo
     * @return Result containing the updated verification level or an error
     */
    suspend fun submitVerificationRequest(
        request: VerificationRequest,
        idPhotoUri: Uri,
        selfieUri: Uri
    ): Result<VerificationLevel>
    
    /**
     * Get the current verification status
     * @return Flow of Result containing the verification level or an error
     */
    fun getVerificationStatus(): Flow<Result<VerificationLevel>>
    
    /**
     * Report a user for inappropriate behavior
     * @param userId ID of the user to report
     * @param reason Reason for the report
     * @param details Additional details
     * @return Result indicating success or failure
     */
    suspend fun reportUser(
        userId: String,
        reason: String,
        details: String? = null
    ): Result<Unit>
}