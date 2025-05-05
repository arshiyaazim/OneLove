package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.UserPreferences
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for user data and operations
 */
interface UserRepository {
    
    /**
     * Get the current logged-in user
     * 
     * @return Flow of Resource containing the user
     */
    fun getCurrentUser(): Flow<Resource<User>>
    
    /**
     * Get a user by ID
     * 
     * @param userId ID of the user to retrieve
     * @return Flow of Resource containing the user
     */
    fun getUserById(userId: String): Flow<Resource<User>>
    
    /**
     * Update the current user's profile
     * 
     * @param firstName New first name (or null to keep current)
     * @param lastName New last name (or null to keep current)
     * @param bio New bio (or null to keep current)
     * @param city New city (or null to keep current)
     * @param country New country (or null to keep current)
     * @param profilePictureUrl New profile picture URL (or null to keep current)
     * @return Flow of Resource indicating success/failure
     */
    fun updateUserProfile(
        firstName: String? = null,
        lastName: String? = null,
        bio: String? = null,
        city: String? = null,
        country: String? = null,
        profilePictureUrl: String? = null
    ): Flow<Resource<Unit>>
    
    /**
     * Update user location
     * 
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @return Flow of Resource indicating success/failure
     */
    fun updateUserLocation(latitude: Double, longitude: Double): Flow<Resource<Unit>>
    
    /**
     * Get current user's preferences
     * 
     * @return Flow of Resource containing the user preferences
     */
    fun getUserPreferences(): Flow<Resource<UserPreferences>>
    
    /**
     * Update user preferences
     * 
     * @param preferences New preferences
     * @return Flow of Resource indicating success/failure
     */
    fun updateUserPreferences(preferences: UserPreferences): Flow<Resource<Unit>>
    
    /**
     * Submit verification document
     * 
     * @param idDocumentUrl URL of the uploaded ID document
     * @return Flow of Resource indicating success/failure
     */
    fun submitVerification(idDocumentUrl: String): Flow<Resource<Unit>>
    
    /**
     * Update user's online status
     * 
     * @param isOnline Whether the user is online
     * @param lastActive Last active timestamp
     * @return Flow of Resource indicating success/failure
     */
    fun updateOnlineStatus(
        isOnline: Boolean,
        lastActive: Date = Date()
    ): Flow<Resource<Unit>>
    
    /**
     * Search users by name or username
     * 
     * @param query Search query
     * @return Flow of Resource containing a list of matching users
     */
    fun searchUsers(query: String): Flow<Resource<List<User>>>
}