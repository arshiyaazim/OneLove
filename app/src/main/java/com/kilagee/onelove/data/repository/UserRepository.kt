package com.kilagee.onelove.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kilagee.onelove.data.model.MembershipTier
import com.kilagee.onelove.data.model.OfferSettings
import com.kilagee.onelove.data.model.PrivacySettings
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val usersCollection = firestore.collection("users")
    
    // Authentication functions
    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String,
        age: Int,
        country: String,
        location: String,
        gender: String
    ): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("User ID is null"))
            
            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("$firstName $lastName")
                .build()
            
            auth.currentUser?.updateProfile(profileUpdates)?.await()
            
            // Create user in Firestore
            val user = User(
                id = userId,
                email = email,
                firstName = firstName,
                lastName = lastName,
                username = username,
                age = age,
                country = country,
                location = location,
                gender = gender
            )
            
            usersCollection.document(userId).set(user.toMap()).await()
            
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            firstName = firebaseUser.displayName?.split(" ")?.firstOrNull() ?: "",
            lastName = firebaseUser.displayName?.split(" ")?.lastOrNull() ?: "",
            profilePictureUrl = firebaseUser.photoUrl?.toString() ?: ""
        )
    }
    
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    // User profile functions
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java) ?: 
                return Result.failure(Exception("User not found"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadProfilePicture(userId: String, imageUri: Uri): Result<String> {
        return try {
            val storageRef = storage.reference.child("profile_pictures/$userId")
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            // Update user document with new profile picture URL
            usersCollection.document(userId)
                .update("profilePictureUrl", downloadUrl)
                .await()
                
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadIdDocument(userId: String, imageUri: Uri): Result<String> {
        return try {
            val storageRef = storage.reference.child("id_documents/$userId")
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            // Update user document with new ID document URL
            usersCollection.document(userId)
                .update("idDocumentUrl", downloadUrl)
                .await()
                
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update("verificationStatus", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateMembershipTier(userId: String, tier: MembershipTier): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update("membershipTier", tier.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePrivacySettings(userId: String, settings: PrivacySettings): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update("privacySettings", settings.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOfferSettings(userId: String, settings: OfferSettings): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update("offerSettings", settings.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addPoints(userId: String, points: Int): Result<Int> {
        return try {
            val userDoc = usersCollection.document(userId).get().await()
            val currentPoints = userDoc.getLong("points")?.toInt() ?: 0
            val newPoints = currentPoints + points
            
            usersCollection.document(userId)
                .update("points", newPoints)
                .await()
                
            Result.success(newPoints)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserSuggestions(
        currentUserId: String,
        limit: Int = 10,
        country: String? = null,
        minAge: Int? = null,
        maxAge: Int? = null,
        gender: String? = null,
        interests: List<String>? = null
    ): Flow<Result<List<User>>> = flow {
        try {
            var query = usersCollection
                .whereNotEqualTo("id", currentUserId)
                .whereEqualTo("privacySettings.showInDiscovery", true)
                
            // Apply filters if provided
            if (country != null) {
                query = query.whereEqualTo("country", country)
            }
            
            if (gender != null) {
                query = query.whereEqualTo("gender", gender)
            }
            
            // Get users
            val result = query.limit(limit.toLong()).get().await()
            
            // Convert to User objects
            val users = result.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            
            // Apply age filter if provided
            var filteredUsers = users
            if (minAge != null || maxAge != null) {
                filteredUsers = users.filter { user ->
                    (minAge == null || user.age >= minAge) && 
                    (maxAge == null || user.age <= maxAge)
                }
            }
            
            // Apply interests filter if provided
            if (!interests.isNullOrEmpty()) {
                filteredUsers = filteredUsers.filter { user ->
                    user.interests.any { it in interests }
                }
            }
            
            emit(Result.success(filteredUsers))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun searchUsers(query: String, limit: Int = 10): Flow<Result<List<User>>> = flow {
        try {
            val result = usersCollection
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(limit.toLong())
                .get()
                .await()
                
            val users = result.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            
            emit(Result.success(users))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
