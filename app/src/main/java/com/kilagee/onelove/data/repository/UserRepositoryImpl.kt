package com.kilagee.onelove.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.kilagee.onelove.data.model.Like
import com.kilagee.onelove.data.model.Match
import com.kilagee.onelove.data.model.Skip
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.model.UserDomain
import com.kilagee.onelove.domain.repository.UserRepository
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository that uses Firebase services
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val LIKES_COLLECTION = "likes"
        private const val MATCHES_COLLECTION = "matches"
        private const val SKIPS_COLLECTION = "skips"
    }
    
    /**
     * Get the current user profile
     */
    override suspend fun getCurrentUser(): Result<UserDomain> {
        val currentUserId = auth.currentUser?.uid ?: return Result.error("User not authenticated")
        
        return try {
            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .get()
                .await()
            
            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                if (user != null) {
                    Result.success(UserDomain.fromDataModel(user))
                } else {
                    Result.error("Failed to parse user data")
                }
            } else {
                Result.error("User not found")
            }
        } catch (e: Exception) {
            Result.error("Error getting user: ${e.message}", e)
        }
    }
    
    /**
     * Get a user by ID
     */
    override suspend fun getUserById(userId: String): Result<UserDomain> {
        return try {
            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            
            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                if (user != null) {
                    Result.success(UserDomain.fromDataModel(user))
                } else {
                    Result.error("Failed to parse user data")
                }
            } else {
                Result.error("User not found")
            }
        } catch (e: Exception) {
            Result.error("Error getting user: ${e.message}", e)
        }
    }
    
    /**
     * Observe the current user profile
     */
    override fun observeCurrentUser(): Flow<Result<UserDomain>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            trySend(Result.error("User not authenticated"))
            close()
            return@callbackFlow
        }
        
        val registration = firestore.collection(USERS_COLLECTION)
            .document(currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.error("Error observing user: ${error.message}", error))
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        trySend(Result.success(UserDomain.fromDataModel(user)))
                    } else {
                        trySend(Result.error("Failed to parse user data"))
                    }
                } else {
                    trySend(Result.error("User not found"))
                }
            }
        
        awaitClose { registration.remove() }
    }
    
    /**
     * Get profiles for discovery
     */
    override suspend fun getDiscoveryProfiles(): List<UserDomain> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        
        try {
            // Get current user for preferences
            val currentUserResult = getCurrentUser()
            if (currentUserResult !is Result.Success) {
                return emptyList()
            }
            
            val currentUser = currentUserResult.data
            
            // Get user's likes, matches, and skips to exclude them
            val likes = firestore.collection(LIKES_COLLECTION)
                .whereEqualTo("fromUserId", currentUserId)
                .get()
                .await()
                .toObjects(Like::class.java)
                .map { it.toUserId }
            
            val matches = firestore.collection(MATCHES_COLLECTION)
                .whereEqualTo("userId1", currentUserId)
                .get()
                .await()
                .toObjects(Match::class.java)
                .map { it.userId2 } +
                    firestore.collection(MATCHES_COLLECTION)
                        .whereEqualTo("userId2", currentUserId)
                        .get()
                        .await()
                        .toObjects(Match::class.java)
                        .map { it.userId1 }
            
            val skips = firestore.collection(SKIPS_COLLECTION)
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()
                .toObjects(Skip::class.java)
                .map { it.skippedUserId }
            
            // Combine all IDs to exclude
            val excludedIds = likes + matches + skips + currentUser.blockedUserIds + listOf(currentUserId)
            
            // Query profiles based on user preferences
            val query = firestore.collection(USERS_COLLECTION)
                .whereEqualTo("isActive", true)
                .whereEqualTo("hideProfile", false)
                
            // Apply filters based on gender preferences if any are specified
            if (currentUser.preferredGenders.isNotEmpty()) {
                val genderValues = currentUser.preferredGenders.map { it.toDataValue() }
                query.whereIn("gender", genderValues)
            }
                
            // Apply age filter
            val minAge = currentUser.preferredAgeRange.first
            val maxAge = currentUser.preferredAgeRange.last
            
            // Execute query
            val profiles = query.get().await()
                .toObjects(User::class.java)
                .filter { user -> 
                    // Apply additional filtering
                    user.id !in excludedIds &&
                    calculateAge(user.birthday?.toDate()) in minAge..maxAge
                }
                .map { UserDomain.fromDataModel(it) }
            
            return profiles
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    /**
     * Calculate age from birthday
     */
    private fun calculateAge(birthDate: java.util.Date?): Int {
        if (birthDate == null) return 0
        
        val today = java.util.Calendar.getInstance()
        val birthCal = java.util.Calendar.getInstance().apply {
            time = birthDate
        }
        
        var age = today.get(java.util.Calendar.YEAR) - birthCal.get(java.util.Calendar.YEAR)
        
        if (today.get(java.util.Calendar.DAY_OF_YEAR) < birthCal.get(java.util.Calendar.DAY_OF_YEAR)) {
            age--
        }
        
        return age
    }
    
    /**
     * Like a profile
     */
    override suspend fun likeProfile(profileId: String, isSuperLike: Boolean): Result<Unit> {
        val currentUserId = auth.currentUser?.uid ?: return Result.error("User not authenticated")
        
        return try {
            val like = Like(
                fromUserId = currentUserId,
                toUserId = profileId,
                createdAt = com.google.firebase.Timestamp.now(),
                isSuperLike = isSuperLike
            )
            
            firestore.collection(LIKES_COLLECTION)
                .add(like)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Error liking profile: ${e.message}", e)
        }
    }
    
    /**
     * Skip a profile
     */
    override suspend fun skipProfile(profileId: String, reason: String?): Result<Unit> {
        val currentUserId = auth.currentUser?.uid ?: return Result.error("User not authenticated")
        
        return try {
            val skip = Skip(
                userId = currentUserId,
                skippedUserId = profileId,
                timestamp = com.google.firebase.Timestamp.now(),
                reason = reason
            )
            
            firestore.collection(SKIPS_COLLECTION)
                .add(skip)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Error skipping profile: ${e.message}", e)
        }
    }
    
    /**
     * Check if there is a match with a profile
     */
    override suspend fun checkForMatch(profileId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false
        
        try {
            // Check if other user has liked current user
            val otherLikes = firestore.collection(LIKES_COLLECTION)
                .whereEqualTo("fromUserId", profileId)
                .whereEqualTo("toUserId", currentUserId)
                .get()
                .await()
            
            if (otherLikes.isEmpty) {
                return false
            }
            
            // Create a match
            val matchId = "$currentUserId-$profileId".split("-").sorted().joinToString("-")
            val match = Match(
                id = matchId,
                userId1 = currentUserId,
                userId2 = profileId,
                matchedAt = com.google.firebase.Timestamp.now(),
                lastInteractionAt = com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(MATCHES_COLLECTION)
                .document(matchId)
                .set(match)
                .await()
            
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * Update user profile
     */
    override suspend fun updateUserProfile(user: UserDomain): Result<UserDomain> {
        val currentUserId = auth.currentUser?.uid ?: return Result.error("User not authenticated")
        
        return try {
            val dataModel = user.toDataModel()
            
            firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .set(dataModel)
                .await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.error("Error updating profile: ${e.message}", e)
        }
    }
    
    /**
     * Update user location
     */
    override suspend fun updateUserLocation(latitude: Double, longitude: Double): Result<Unit> {
        val currentUserId = auth.currentUser?.uid ?: return Result.error("User not authenticated")
        
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .update(
                    "location", GeoPoint(latitude, longitude),
                    "lastLocationUpdate", com.google.firebase.Timestamp.now()
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Error updating location: ${e.message}", e)
        }
    }
    
    /**
     * Get matched users
     */
    override suspend fun getMatches(): List<UserDomain> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        
        try {
            // Get matches where current user is user1
            val matches1 = firestore.collection(MATCHES_COLLECTION)
                .whereEqualTo("userId1", currentUserId)
                .whereEqualTo("status", Match.STATUS_ACTIVE)
                .get()
                .await()
                .toObjects(Match::class.java)
                .map { it.userId2 }
            
            // Get matches where current user is user2
            val matches2 = firestore.collection(MATCHES_COLLECTION)
                .whereEqualTo("userId2", currentUserId)
                .whereEqualTo("status", Match.STATUS_ACTIVE)
                .get()
                .await()
                .toObjects(Match::class.java)
                .map { it.userId1 }
            
            // Combine all matched user IDs
            val matchedUserIds = matches1 + matches2
            
            // Get user profiles for matched users
            val matchedUsers = matchedUserIds.mapNotNull { userId ->
                val userResult = getUserById(userId)
                userResult.getOrNull()
            }
            
            return matchedUsers
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    /**
     * Observe matched users
     */
    override fun observeMatches(): Flow<List<UserDomain>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        // Observe matches where current user is user1
        val registration1 = firestore.collection(MATCHES_COLLECTION)
            .whereEqualTo("userId1", currentUserId)
            .whereEqualTo("status", Match.STATUS_ACTIVE)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                
                // Process updates in a full refresh for simplicity
                refreshMatches(currentUserId)
            }
        
        // Observe matches where current user is user2
        val registration2 = firestore.collection(MATCHES_COLLECTION)
            .whereEqualTo("userId2", currentUserId)
            .whereEqualTo("status", Match.STATUS_ACTIVE)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                
                // Process updates in a full refresh for simplicity
                refreshMatches(currentUserId)
            }
        
        suspend fun refreshMatches(userId: String) {
            val matches = getMatches()
            trySend(matches)
        }
        
        // Initial load
        refreshMatches(currentUserId)
        
        awaitClose { 
            registration1.remove()
            registration2.remove()
        }
    }
    
    /**
     * Block a user
     */
    override suspend fun blockUser(userId: String): Result<Unit> {
        val currentUserId = auth.currentUser?.uid ?: return Result.error("User not authenticated")
        
        return try {
            // Update current user's blocked list
            firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .update("blockedUsers", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .await()
            
            // Update match status if exists
            val matchId = "$currentUserId-$userId".split("-").sorted().joinToString("-")
            val matchDoc = firestore.collection(MATCHES_COLLECTION)
                .document(matchId)
                .get()
                .await()
            
            if (matchDoc.exists()) {
                firestore.collection(MATCHES_COLLECTION)
                    .document(matchId)
                    .update("status", Match.STATUS_BLOCKED)
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Error blocking user: ${e.message}", e)
        }
    }
    
    /**
     * Unblock a user
     */
    override suspend fun unblockUser(userId: String): Result<Unit> {
        val currentUserId = auth.currentUser?.uid ?: return Result.error("User not authenticated")
        
        return try {
            // Update current user's blocked list
            firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .update("blockedUsers", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Error unblocking user: ${e.message}", e)
        }
    }
    
    /**
     * Report a user
     */
    override suspend fun reportUser(userId: String, reason: String, details: String?): Result<Unit> {
        val currentUserId = auth.currentUser?.uid ?: return Result.error("User not authenticated")
        
        return try {
            val report = hashMapOf(
                "reporterId" to currentUserId,
                "reportedUserId" to userId,
                "reason" to reason,
                "details" to (details ?: ""),
                "timestamp" to com.google.firebase.Timestamp.now(),
                "resolved" to false
            )
            
            firestore.collection("reports")
                .add(report)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Error reporting user: ${e.message}", e)
        }
    }
}