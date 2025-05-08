package com.kilagee.onelove.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Get the current authenticated user
     * @return Flow of Result containing the current FirebaseUser or an error
     */
    fun getCurrentUser(): Flow<Result<FirebaseUser?>>
    
    /**
     * Get the current user's profile data
     * @return Flow of Result containing the current User profile or an error
     */
    fun getCurrentUserProfile(): Flow<Result<User?>>
    
    /**
     * Sign in with email and password
     * @param email User's email
     * @param password User's password
     * @return Result containing the FirebaseUser or an error
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser>
    
    /**
     * Register a new account with email and password
     * @param email User's email
     * @param password User's password
     * @param name User's name
     * @return Result containing the FirebaseUser or an error
     */
    suspend fun registerWithEmailAndPassword(
        email: String, 
        password: String,
        name: String
    ): Result<FirebaseUser>
    
    /**
     * Send a password reset email
     * @param email User's email
     * @return Result indicating success or failure
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    /**
     * Sign out the current user
     * @return Result indicating success or failure
     */
    suspend fun signOut(): Result<Unit>
    
    /**
     * Update the user's profile information
     * @param user Updated user profile data
     * @return Result containing the updated User or an error
     */
    suspend fun updateUserProfile(user: User): Result<User>
    
    /**
     * Delete the current user's account
     * @return Result indicating success or failure
     */
    suspend fun deleteAccount(): Result<Unit>
    
    /**
     * Send email verification
     * @return Result indicating success or failure
     */
    suspend fun sendEmailVerification(): Result<Unit>
    
    /**
     * Verify the user's phone number
     * @param phoneNumber User's phone number
     * @param verificationCode Verification code received via SMS
     * @return Result indicating success or failure
     */
    suspend fun verifyPhoneNumber(phoneNumber: String, verificationCode: String): Result<Unit>
    
    /**
     * Link an anonymous account with email and password
     * @param email User's email
     * @param password User's password
     * @return Result containing the linked FirebaseUser or an error
     */
    suspend fun linkAnonymousAccountWithEmailAndPassword(
        email: String,
        password: String
    ): Result<FirebaseUser>
    
    /**
     * Check if a user is already signed in
     * @return Boolean indicating if a user is signed in
     */
    fun isUserSignedIn(): Boolean
    
    /**
     * Check if the current user's email is verified
     * @return Boolean indicating if the email is verified or null if no user is signed in
     */
    fun isEmailVerified(): Boolean?
    
    /**
     * Reauthenticate the current user with their password
     * @param password User's current password
     * @return Result indicating success or failure
     */
    suspend fun reauthenticateWithPassword(password: String): Result<Unit>
    
    /**
     * Update the user's password
     * @param newPassword User's new password
     * @return Result indicating success or failure
     */
    suspend fun updatePassword(newPassword: String): Result<Unit>
    
    /**
     * Update the user's email address
     * @param newEmail User's new email address
     * @return Result indicating success or failure
     */
    suspend fun updateEmail(newEmail: String): Result<Unit>
}