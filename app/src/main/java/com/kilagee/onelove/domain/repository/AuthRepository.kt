package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    
    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>
    
    /**
     * Sign up with email and password
     */
    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        displayName: String,
        phoneNumber: String? = null
    ): Result<User>
    
    /**
     * Sign in with Google account
     */
    suspend fun signInWithGoogle(idToken: String): Result<User>
    
    /**
     * Sign in with Facebook account
     */
    suspend fun signInWithFacebook(accessToken: String): Result<User>
    
    /**
     * Sign in with Apple account
     */
    suspend fun signInWithApple(idToken: String, nonce: String): Result<User>
    
    /**
     * Sign in anonymously
     */
    suspend fun signInAnonymously(): Result<User>
    
    /**
     * Send email verification
     */
    suspend fun sendEmailVerification(): Result<Unit>
    
    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    /**
     * Verify password reset code
     */
    suspend fun verifyPasswordResetCode(code: String): Result<String> // Returns email
    
    /**
     * Confirm password reset
     */
    suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit>
    
    /**
     * Send phone verification code
     */
    suspend fun sendPhoneVerificationCode(phoneNumber: String): Result<Unit>
    
    /**
     * Verify phone with code
     */
    suspend fun verifyPhoneWithCode(verificationId: String, code: String): Result<Unit>
    
    /**
     * Re-authenticate user (for sensitive operations)
     */
    suspend fun reauthenticate(password: String): Result<Unit>
    
    /**
     * Change password
     */
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
    
    /**
     * Link anonymous account with email and password
     */
    suspend fun linkWithEmailAndPassword(email: String, password: String): Result<User>
    
    /**
     * Link account with Google
     */
    suspend fun linkWithGoogle(idToken: String): Result<User>
    
    /**
     * Link account with Facebook
     */
    suspend fun linkWithFacebook(accessToken: String): Result<User>
    
    /**
     * Link account with Apple
     */
    suspend fun linkWithApple(idToken: String, nonce: String): Result<User>
    
    /**
     * Link account with phone number
     */
    suspend fun linkWithPhoneNumber(verificationId: String, code: String): Result<User>
    
    /**
     * Sign out
     */
    suspend fun signOut(): Result<Unit>
    
    /**
     * Delete account
     */
    suspend fun deleteAccount(): Result<Unit>
    
    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean
    
    /**
     * Get current user
     */
    suspend fun getCurrentUser(): Result<User?>
    
    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String?
    
    /**
     * Get current user as a flow
     */
    fun getCurrentUserFlow(): Flow<Result<User?>>
    
    /**
     * Check if email is verified
     */
    suspend fun isEmailVerified(): Result<Boolean>
    
    /**
     * Check if phone is verified
     */
    suspend fun isPhoneVerified(): Result<Boolean>
    
    /**
     * Refresh authentication token
     */
    suspend fun refreshToken(): Result<String> // Returns new token
    
    /**
     * Get authentication token
     */
    suspend fun getIdToken(forceRefresh: Boolean = false): Result<String>
}