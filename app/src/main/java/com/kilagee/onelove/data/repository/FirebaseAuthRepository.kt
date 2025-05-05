package com.kilagee.onelove.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kilagee.onelove.data.database.dao.UserDao
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.VerificationStatus
import com.kilagee.onelove.domain.model.Resource
import com.kilagee.onelove.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userDao: UserDao
) : AuthRepository {
    
    private val usersCollection = firestore.collection("users")
    private val profileImagesRef = storage.reference.child("profile_images")
    private val idDocumentsRef = storage.reference.child("id_documents")
    
    override fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let { firebaseUser ->
                val userId = firebaseUser.uid
                
                // Get user from Firestore
                val userDocument = usersCollection.document(userId).get().await()
                
                if (userDocument.exists()) {
                    val user = userDocument.toObject(User::class.java)
                    user?.let {
                        // Save user to local database
                        userDao.insertUser(it)
                        emit(Resource.success(it))
                    } ?: emit(Resource.error("Failed to parse user data"))
                } else {
                    emit(Resource.error("User data not found"))
                }
            } ?: emit(Resource.error("Authentication failed"))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error during login", e))
        }
    }
    
    override fun register(
        email: String, 
        password: String, 
        user: User, 
        profileImageUri: Uri?, 
        idDocumentUri: Uri?
    ): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        
        try {
            // Create user in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            
            authResult.user?.let { firebaseUser ->
                val userId = firebaseUser.uid
                
                // Update user with proper ID
                val updatedUser = user.copy(
                    id = userId,
                    createdAt = Date(),
                    verificationStatus = VerificationStatus.PENDING
                )
                
                // Upload profile image if provided
                var profileImageUrl: String? = null
                if (profileImageUri != null) {
                    val imageRef = profileImagesRef.child("$userId.jpg")
                    imageRef.putFile(profileImageUri).await()
                    profileImageUrl = imageRef.downloadUrl.await().toString()
                }
                
                // Upload ID document if provided
                var idDocumentUrl: String? = null
                if (idDocumentUri != null) {
                    val docRef = idDocumentsRef.child("$userId.jpg")
                    docRef.putFile(idDocumentUri).await()
                    idDocumentUrl = docRef.downloadUrl.await().toString()
                }
                
                // Update user with profile image URL and ID document URL
                val finalUser = updatedUser.copy(
                    profileImageUrl = profileImageUrl,
                    idDocumentUrl = idDocumentUrl
                )
                
                // Save user to Firestore
                usersCollection.document(userId).set(finalUser).await()
                
                // Save user to local database
                userDao.insertUser(finalUser)
                
                emit(Resource.success(finalUser))
            } ?: emit(Resource.error("Failed to create user"))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error during registration", e))
        }
    }
    
    override fun resetPassword(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error during password reset", e))
        }
    }
    
    override fun logout(): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        
        try {
            auth.signOut()
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error during logout", e))
        }
    }
    
    override fun getCurrentUser(): Flow<Resource<User?>> = flow {
        emit(Resource.loading())
        
        try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val userId = firebaseUser.uid
                
                // First try to get from local database
                val localUser = userDao.getUserById(userId)
                if (localUser != null) {
                    emit(Resource.success(localUser))
                    
                    // Sync with Firestore in background
                    try {
                        val userDocument = usersCollection.document(userId).get().await()
                        if (userDocument.exists()) {
                            val firestoreUser = userDocument.toObject(User::class.java)
                            firestoreUser?.let {
                                // Update local database with latest data
                                userDao.insertUser(it)
                                emit(Resource.success(it))
                            }
                        }
                    } catch (e: Exception) {
                        // Silently fail syncing, we already have local data
                    }
                } else {
                    // If not in local database, get from Firestore
                    val userDocument = usersCollection.document(userId).get().await()
                    
                    if (userDocument.exists()) {
                        val user = userDocument.toObject(User::class.java)
                        user?.let {
                            // Save user to local database
                            userDao.insertUser(it)
                            emit(Resource.success(it))
                        } ?: emit(Resource.error("Failed to parse user data"))
                    } else {
                        emit(Resource.error("User data not found"))
                    }
                }
            } else {
                emit(Resource.success(null))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error getting current user", e))
        }
    }
    
    override fun updateUserProfile(user: User, profileImageUri: Uri?): Flow<Resource<User>> = flow {
        emit(Resource.loading())
        
        try {
            val userId = user.id ?: auth.currentUser?.uid
            if (userId == null) {
                emit(Resource.error("User not authenticated"))
                return@flow
            }
            
            // Upload new profile image if provided
            var profileImageUrl = user.profileImageUrl
            if (profileImageUri != null) {
                val imageRef = profileImagesRef.child("$userId.jpg")
                imageRef.putFile(profileImageUri).await()
                profileImageUrl = imageRef.downloadUrl.await().toString()
            }
            
            // Update user with profile image URL
            val updatedUser = user.copy(
                profileImageUrl = profileImageUrl,
                updatedAt = Date()
            )
            
            // Update user in Firestore
            usersCollection.document(userId).set(updatedUser).await()
            
            // Update user in local database
            userDao.insertUser(updatedUser)
            
            emit(Resource.success(updatedUser))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error updating profile", e))
        }
    }
    
    override fun deleteAccount(): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        
        try {
            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid
                
                // Delete user data from Firestore
                usersCollection.document(userId).delete().await()
                
                // Delete profile image from Storage
                try {
                    profileImagesRef.child("$userId.jpg").delete().await()
                } catch (e: Exception) {
                    // Ignore if profile image doesn't exist
                }
                
                // Delete ID document from Storage
                try {
                    idDocumentsRef.child("$userId.jpg").delete().await()
                } catch (e: Exception) {
                    // Ignore if ID document doesn't exist
                }
                
                // Delete user from Auth
                user.delete().await()
                
                // Delete user from local database
                userDao.deleteUserById(userId)
                
                emit(Resource.success(Unit))
            } else {
                emit(Resource.error("User not authenticated"))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error deleting account", e))
        }
    }
    
    override fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Listen for authentication state changes.
     */
    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        
        auth.addAuthStateListener(authStateListener)
        
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
}