package com.kilagee.onelove.domain.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication-related operations
 */
interface AuthRepository {
    
    /**
     * Get the currently authenticated user
     * 
     * @return Flow of the current FirebaseUser or null if not authenticated
     */
    fun getCurrentUser(): Flow<FirebaseUser?>
    
    /**
     * Get user profile by ID
     * 
     * @param userId User ID to get profile for, or null for current user
     * @return Flow of Result containing the User object
     */
    fun getUserProfile(userId: String? = null): Flow<Result<User>>
    
    /**
     * Sign in with email and password
     * 
     * @param email User's email
     * @param password User's password
     * @return Result of the sign-in operation
     */
    suspend fun signInWithEmailPassword(email: String, password: String): Result<FirebaseUser>
    
    /**
     * Sign in with credential (e.g., Google, Facebook)
     * 
     * @param credential Auth credential
     * @return Result of the sign-in operation
     */
    suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser>
    
    /**
     * Sign up with email and password
     * 
     * @param email User's email
     * @param password User's password
     * @param name User's display name
     * @return Result of the sign-up operation
     */
    suspend fun signUpWithEmailPassword(
        email: String, 
        password: String, 
        name: String
    ): Result<FirebaseUser>
    
    /**
     * Send password reset email
     * 
     * @param email User's email
     * @return Result of the operation
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    /**
     * Verify email
     * 
     * @param code Verification code
     * @return Result of the verification operation
     */
    suspend fun verifyEmail(code: String): Result<Unit>
    
    /**
     * Update user profile
     * 
     * @param user Updated user profile
     * @return Result of the update operation
     */
    suspend fun updateUserProfile(user: User): Result<User>
    
    /**
     * Update user's FCM token
     * 
     * @param token FCM token
     * @return Result of the update operation
     */
    suspend fun updateFcmToken(token: String): Result<Unit>
    
    /**
     * Sign out the current user
     * 
     * @return Result of the sign-out operation
     */
    suspend fun signOut(): Result<Unit>
    
    /**
     * Delete the current user's account
     * 
     * @return Result of the delete operation
     */
    suspend fun deleteAccount(): Result<Unit>
}