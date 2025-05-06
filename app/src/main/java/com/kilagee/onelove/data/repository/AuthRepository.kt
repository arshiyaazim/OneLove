package com.kilagee.onelove.data.repository

import com.google.firebase.auth.FirebaseUser
import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    
    /**
     * Get the current logged-in Firebase user
     * @return The current Firebase user or null if not logged in
     */
    fun getCurrentFirebaseUser(): FirebaseUser?
    
    /**
     * Get the current user's ID
     * @return The user ID or null if not logged in
     */
    fun getCurrentUserId(): String?
    
    /**
     * Check if the user is logged in
     * @return True if logged in, false otherwise
     */
    fun isLoggedIn(): Boolean
    
    /**
     * Sign in with email and password
     * @param email User's email
     * @param password User's password
     * @return Result containing the Firebase user or error
     */
    suspend fun signInWithEmailPassword(email: String, password: String): Result<FirebaseUser>
    
    /**
     * Sign up with email and password
     * @param email User's email
     * @param password User's password
     * @param displayName User's display name
     * @return Result containing the Firebase user or error
     */
    suspend fun signUpWithEmailPassword(
        email: String, 
        password: String, 
        displayName: String
    ): Result<FirebaseUser>
    
    /**
     * Sign out the current user
     * @return Result indicating success or error
     */
    suspend fun signOut(): Result<Unit>
    
    /**
     * Send password reset email
     * @param email User's email
     * @return Result indicating success or error
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    /**
     * Verify email
     * @return Result indicating success or error
     */
    suspend fun sendEmailVerification(): Result<Unit>
    
    /**
     * Update user's password
     * @param currentPassword Current password
     * @param newPassword New password
     * @return Result indicating success or error
     */
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit>
    
    /**
     * Update user's email
     * @param newEmail New email
     * @param password Current password
     * @return Result indicating success or error
     */
    suspend fun updateEmail(newEmail: String, password: String): Result<Unit>
    
    /**
     * Delete user account
     * @param password Current password
     * @return Result indicating success or error
     */
    suspend fun deleteAccount(password: String): Result<Unit>
    
    /**
     * Get authentication state as a Flow
     * @return Flow emitting the current Firebase user or null
     */
    fun getAuthStateFlow(): Flow<FirebaseUser?>
    
    /**
     * Get current user from Firestore
     * @return Result containing the user or error
     */
    suspend fun getCurrentUserProfile(): Result<User>
    
    /**
     * Get current user from Firestore as Flow
     * @return Flow emitting Result containing the user or error
     */
    fun getCurrentUserProfileFlow(): Flow<Result<User>>
}