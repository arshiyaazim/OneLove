package com.kilagee.onelove.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.UserSettings
import com.kilagee.onelove.domain.model.UserDomain
import com.kilagee.onelove.domain.repository.AuthRepository
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository that uses Firebase Authentication
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    /**
     * Sign in with email and password
     */
    override suspend fun signInWithEmailPassword(email: String, password: String): Result<UserDomain> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return Result.error("Authentication failed")
            
            // Get user profile from Firestore
            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .await()
            
            if (userDoc.exists()) {
                val userData = userDoc.toObject(User::class.java)
                if (userData != null) {
                    // Update last login time
                    firestore.collection(USERS_COLLECTION)
                        .document(user.uid)
                        .update("lastLogin", com.google.firebase.Timestamp.now())
                        .await()
                    
                    Result.success(UserDomain.fromDataModel(userData))
                } else {
                    Result.error("Failed to parse user data")
                }
            } else {
                Result.error("User profile not found")
            }
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> 
                    Result.error("Invalid email or password", e)
                else -> Result.error("Login failed: ${e.message}", e)
            }
        }
    }

    /**
     * Sign up with email and password
     */
    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Result<UserDomain> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return Result.error("Authentication failed")
            
            // Create user profile in Firestore
            val newUser = User(
                id = user.uid,
                email = email,
                name = name,
                phoneNumber = phone,
                joinDate = com.google.firebase.Timestamp.now(),
                lastLogin = com.google.firebase.Timestamp.now(),
                isActive = true,
                settings = UserSettings()
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(newUser)
                .await()
            
            Result.success(UserDomain.fromDataModel(newUser))
        } catch (e: Exception) {
            Result.error("Registration failed: ${e.message}", e)
        }
    }

    /**
     * Sign in with Google
     */
    override suspend fun signInWithGoogle(idToken: String): Result<UserDomain> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: return Result.error("Authentication failed")
            
            // Check if user already exists in Firestore
            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .await()
            
            if (userDoc.exists()) {
                // User exists, update last login
                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .update("lastLogin", com.google.firebase.Timestamp.now())
                    .await()
                
                val userData = userDoc.toObject(User::class.java)
                if (userData != null) {
                    Result.success(UserDomain.fromDataModel(userData))
                } else {
                    Result.error("Failed to parse user data")
                }
            } else {
                // Create new user profile
                val newUser = User(
                    id = user.uid,
                    email = user.email ?: "",
                    name = user.displayName ?: "",
                    photoUrl = user.photoUrl?.toString(),
                    joinDate = com.google.firebase.Timestamp.now(),
                    lastLogin = com.google.firebase.Timestamp.now(),
                    isActive = true,
                    settings = UserSettings()
                )
                
                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .set(newUser)
                    .await()
                
                Result.success(UserDomain.fromDataModel(newUser))
            }
        } catch (e: Exception) {
            Result.error("Google sign-in failed: ${e.message}", e)
        }
    }

    /**
     * Sign in with phone
     */
    override suspend fun signInWithPhone(
        phone: String,
        verificationId: String,
        code: String
    ): Result<UserDomain> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: return Result.error("Authentication failed")
            
            // Check if user already exists in Firestore
            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .await()
            
            if (userDoc.exists()) {
                // User exists, update last login
                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .update("lastLogin", com.google.firebase.Timestamp.now())
                    .await()
                
                val userData = userDoc.toObject(User::class.java)
                if (userData != null) {
                    Result.success(UserDomain.fromDataModel(userData))
                } else {
                    Result.error("Failed to parse user data")
                }
            } else {
                // Create new user profile
                val newUser = User(
                    id = user.uid,
                    phoneNumber = phone,
                    joinDate = com.google.firebase.Timestamp.now(),
                    lastLogin = com.google.firebase.Timestamp.now(),
                    isActive = true,
                    settings = UserSettings()
                )
                
                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .set(newUser)
                    .await()
                
                Result.success(UserDomain.fromDataModel(newUser))
            }
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> 
                    Result.error("Invalid verification code", e)
                else -> Result.error("Phone sign-in failed: ${e.message}", e)
            }
        }
    }

    /**
     * Send phone verification code
     * This is a placeholder as the actual implementation would involve callbacks
     * which are handled differently in a real app
     */
    override suspend fun sendPhoneVerificationCode(phone: String): Result<String> {
        // Note: In a real app, this would be implemented with PhoneAuthProvider.verifyPhoneNumber
        // which uses callbacks, not coroutines. The actual implementation would be in the activity
        // or fragment using the PhoneAuthProvider callbacks.
        return Result.error("This method should be implemented in the UI layer with callbacks")
    }

    /**
     * Reset password
     */
    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Password reset failed: ${e.message}", e)
        }
    }

    /**
     * Sign out
     */
    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Sign out failed: ${e.message}", e)
        }
    }

    /**
     * Get current authenticated user
     */
    override suspend fun getCurrentUser(): Result<UserDomain> {
        val firebaseUser = auth.currentUser
            ?: return Result.error("No authenticated user")
        
        return try {
            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .get()
                .await()
            
            if (userDoc.exists()) {
                val userData = userDoc.toObject(User::class.java)
                if (userData != null) {
                    Result.success(UserDomain.fromDataModel(userData))
                } else {
                    Result.error("Failed to parse user data")
                }
            } else {
                Result.error("User profile not found")
            }
        } catch (e: Exception) {
            Result.error("Error getting user: ${e.message}", e)
        }
    }

    /**
     * Observe authentication state
     */
    override fun observeAuthState(): Flow<Result<UserDomain?>> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                trySend(Result.success(null))
                return@AuthStateListener
            }
            
            // Get user profile from Firestore
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = document.toObject(User::class.java)
                        if (userData != null) {
                            trySend(Result.success(UserDomain.fromDataModel(userData)))
                        } else {
                            trySend(Result.error("Failed to parse user data"))
                        }
                    } else {
                        trySend(Result.error("User profile not found"))
                    }
                }
                .addOnFailureListener { e ->
                    trySend(Result.error("Error getting user: ${e.message}", e))
                }
        }
        
        auth.addAuthStateListener(authStateListener)
        
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * Delete account
     */
    override suspend fun deleteAccount(): Result<Unit> {
        val currentUser = auth.currentUser
            ?: return Result.error("No authenticated user")
        
        return try {
            // Delete user data from Firestore
            firestore.collection(USERS_COLLECTION)
                .document(currentUser.uid)
                .delete()
                .await()
            
            // Delete user account
            currentUser.delete().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Account deletion failed: ${e.message}", e)
        }
    }

    /**
     * Update password
     */
    override suspend fun updatePassword(oldPassword: String, newPassword: String): Result<Unit> {
        val currentUser = auth.currentUser
            ?: return Result.error("No authenticated user")
        
        return try {
            // Re-authenticate user
            val email = currentUser.email
                ?: return Result.error("User has no email")
            
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            currentUser.reauthenticate(credential).await()
            
            // Update password
            currentUser.updatePassword(newPassword).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> 
                    Result.error("Incorrect current password", e)
                else -> Result.error("Password update failed: ${e.message}", e)
            }
        }
    }

    /**
     * Link phone number to account
     */
    override suspend fun linkPhoneNumber(
        phone: String,
        verificationId: String,
        code: String
    ): Result<Unit> {
        val currentUser = auth.currentUser
            ?: return Result.error("No authenticated user")
        
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            currentUser.linkWithCredential(credential).await()
            
            // Update user profile in Firestore
            firestore.collection(USERS_COLLECTION)
                .document(currentUser.uid)
                .update("phoneNumber", phone)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> 
                    Result.error("Invalid verification code", e)
                else -> Result.error("Phone linking failed: ${e.message}", e)
            }
        }
    }
}