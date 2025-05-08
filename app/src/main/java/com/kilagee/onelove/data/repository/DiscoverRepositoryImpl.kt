package com.kilagee.onelove.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kilagee.onelove.data.local.UserDao
import com.kilagee.onelove.data.model.Match
import com.kilagee.onelove.data.model.MatchStatus
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.repository.AuthRepository
import com.kilagee.onelove.domain.repository.DiscoverRepository
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

/**
 * Repository implementation for discover-related functions
 */
class DiscoverRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val userDao: UserDao
) : DiscoverRepository {

    private val usersCollection = firestore.collection("users")
    private val matchesCollection = firestore.collection("matches")
    private val skipsCollection = firestore.collection("skips")
    
    /**
     * Get potential matches based on user preferences
     */
    override fun getPotentialMatches(): Flow<Result<List<User>>> = callbackFlow {
        trySend(Result.Loading)
        
        try {
            val currentUserId = authRepository.getCurrentUserId()
            
            if (currentUserId == null) {
                trySend(Result.Error("User not authenticated"))
                close()
                return@callbackFlow
            }
            
            // Get the current user to check preferences
            val currentUserDoc = usersCollection.document(currentUserId).get().await()
            val currentUser = currentUserDoc.toObject(User::class.java)
            
            if (currentUser == null) {
                trySend(Result.Error("Current user not found"))
                close()
                return@callbackFlow
            }
            
            // Prepare query based on preferences
            var query = usersCollection.limit(50)
            
            // Filter by age if preferences exist
            if (currentUser.minAgePreference != null && currentUser.maxAgePreference != null) {
                query = query.whereGreaterThanOrEqualTo("age", currentUser.minAgePreference)
                    .whereLessThanOrEqualTo("age", currentUser.maxAgePreference)
            }
            
            // Get all the users that match preferences
            val querySnapshot = query.get().await()
            
            // Get IDs of users that current user has already interacted with
            val matchedUsersQuery = matchesCollection
                .whereEqualTo("userId", currentUserId)
                .get().await()
                
            val matchedUserIds = matchedUsersQuery.documents.mapNotNull { 
                it.getString("matchedUserId") 
            }.toSet()
            
            val incomingMatchesQuery = matchesCollection
                .whereEqualTo("matchedUserId", currentUserId)
                .get().await()
                
            val incomingMatchUserIds = incomingMatchesQuery.documents.mapNotNull { 
                it.getString("userId") 
            }.toSet()
            
            // Get IDs of users that current user has skipped
            val skippedUsersQuery = skipsCollection
                .whereEqualTo("userId", currentUserId)
                .get().await()
                
            val skippedUserIds = skippedUsersQuery.documents.mapNotNull { 
                it.getString("skippedUserId") 
            }.toSet()
            
            // Combine all IDs to exclude
            val excludeIds = matchedUserIds + incomingMatchUserIds + skippedUserIds + setOf(currentUserId)
            
            // Filter out users that current user has already interacted with
            val potentialMatches = querySnapshot.documents
                .mapNotNull { document -> 
                    document.toObject(User::class.java)?.takeIf { 
                        !excludeIds.contains(it.id) 
                    }
                }
            
            // Filter by distance if maxDistance preference exists
            val filteredByDistance = if (currentUser.maxDistance != null && 
                currentUser.latitude != null && 
                currentUser.longitude != null
            ) {
                potentialMatches.filter { user ->
                    if (user.latitude != null && user.longitude != null) {
                        calculateDistance(
                            currentUser.latitude, 
                            currentUser.longitude,
                            user.latitude, 
                            user.longitude
                        ) <= currentUser.maxDistance
                    } else {
                        true // Include users without location
                    }
                }
            } else {
                potentialMatches
            }
            
            // Cache users in local database
            filteredByDistance.forEach { user ->
                userDao.insertUser(user)
            }
            
            trySend(Result.Success(filteredByDistance))
        } catch (e: Exception) {
            Timber.e(e, "Error getting potential matches")
            trySend(Result.Error("Failed to get potential matches: ${e.message}"))
        }
        
        awaitClose()
    }.catch { e ->
        Timber.e(e, "Exception in getPotentialMatches flow")
        emit(Result.Error("Exception: ${e.message}"))
    }
    
    /**
     * Create a new match
     */
    override suspend fun createMatch(match: Match): Result<Match> {
        return try {
            matchesCollection.document(match.id).set(match).await()
            Result.Success(match)
        } catch (e: Exception) {
            Timber.e(e, "Error creating match")
            Result.Error("Failed to create match: ${e.message}")
        }
    }
    
    /**
     * Check if this is a mutual match
     */
    override suspend fun checkForMutualMatch(userId: String, potentialMatchUserId: String): Result<Boolean> {
        return try {
            // Check if the other user has already liked the current user
            val query = matchesCollection
                .whereEqualTo("userId", potentialMatchUserId)
                .whereEqualTo("matchedUserId", userId)
                .whereEqualTo("status", MatchStatus.PENDING.name)
                .get()
                .await()
            
            if (query.documents.isNotEmpty()) {
                // It's a mutual match!
                val otherMatchId = query.documents[0].id
                
                // Update both matches to ACTIVE
                matchesCollection.document(otherMatchId)
                    .update("status", MatchStatus.ACTIVE.name, "updatedAt", Date())
                    .await()
                
                // Find the current user's match
                val currentUserMatchQuery = matchesCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("matchedUserId", potentialMatchUserId)
                    .get()
                    .await()
                
                if (currentUserMatchQuery.documents.isNotEmpty()) {
                    val currentUserMatchId = currentUserMatchQuery.documents[0].id
                    matchesCollection.document(currentUserMatchId)
                        .update("status", MatchStatus.ACTIVE.name, "updatedAt", Date())
                        .await()
                }
                
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking for mutual match")
            Result.Error("Failed to check for mutual match: ${e.message}")
        }
    }
    
    /**
     * Skip a user - don't show them again
     */
    override suspend fun skipUser(userId: String, skippedUserId: String): Result<Unit> {
        return try {
            val skip = hashMapOf(
                "userId" to userId,
                "skippedUserId" to skippedUserId,
                "timestamp" to Date()
            )
            
            val skipId = "$userId-$skippedUserId"
            skipsCollection.document(skipId).set(skip).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error skipping user")
            Result.Error("Failed to skip user: ${e.message}")
        }
    }
    
    /**
     * Get matches for a user
     */
    override fun getMatches(userId: String): Flow<Result<List<Match>>> = callbackFlow {
        trySend(Result.Loading)
        
        try {
            // Get matches where the user is either the initiator or the matched
            val query = matchesCollection
                .whereEqualTo("status", MatchStatus.ACTIVE.name)
                .whereIn("userId", listOf(userId))
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                
            val listener = query.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    trySend(Result.Error("Failed to listen for matches: ${exception.message}"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val matches = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Match::class.java)
                    }
                    
                    trySend(Result.Success(matches))
                }
            }
            
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Timber.e(e, "Error getting matches")
            trySend(Result.Error("Failed to get matches: ${e.message}"))
            close()
        }
    }.catch { e ->
        Timber.e(e, "Exception in getMatches flow")
        emit(Result.Error("Exception: ${e.message}"))
    }
    
    /**
     * Get users who liked the current user
     */
    override fun getLikes(userId: String): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)
        
        try {
            // Get pending matches where the current user is the matched user
            val query = matchesCollection
                .whereEqualTo("matchedUserId", userId)
                .whereEqualTo("status", MatchStatus.PENDING.name)
                .get()
                .await()
            
            // Get the user IDs who liked the current user
            val likerIds = query.documents.mapNotNull { doc ->
                doc.getString("userId")
            }
            
            if (likerIds.isEmpty()) {
                emit(Result.Success(emptyList()))
                return@flow
            }
            
            // Get the users
            val likers = mutableListOf<User>()
            for (likerId in likerIds) {
                val userDoc = usersCollection.document(likerId).get().await()
                val user = userDoc.toObject(User::class.java)
                if (user != null) {
                    likers.add(user)
                }
            }
            
            emit(Result.Success(likers))
        } catch (e: Exception) {
            Timber.e(e, "Error getting likes")
            emit(Result.Error("Failed to get likes: ${e.message}"))
        }
    }.catch { e ->
        Timber.e(e, "Exception in getLikes flow")
        emit(Result.Error("Exception: ${e.message}"))
    }
    
    /**
     * Get match details
     */
    override fun getMatchById(matchId: String): Flow<Result<Match>> = callbackFlow {
        trySend(Result.Loading)
        
        try {
            val listener = matchesCollection.document(matchId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        trySend(Result.Error("Failed to listen for match: ${exception.message}"))
                        return@addSnapshotListener
                    }
                    
                    if (snapshot != null && snapshot.exists()) {
                        val match = snapshot.toObject(Match::class.java)
                        if (match != null) {
                            trySend(Result.Success(match))
                        } else {
                            trySend(Result.Error("Failed to parse match data"))
                        }
                    } else {
                        trySend(Result.Error("Match not found"))
                    }
                }
            
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Timber.e(e, "Error getting match")
            trySend(Result.Error("Failed to get match: ${e.message}"))
            close()
        }
    }.catch { e ->
        Timber.e(e, "Exception in getMatchById flow")
        emit(Result.Error("Exception: ${e.message}"))
    }
    
    /**
     * Update match status
     */
    override suspend fun updateMatchStatus(matchId: String, status: MatchStatus): Result<Match> {
        return try {
            val matchDoc = matchesCollection.document(matchId).get().await()
            if (!matchDoc.exists()) {
                return Result.Error("Match not found")
            }
            
            matchesCollection.document(matchId)
                .update("status", status.name, "updatedAt", Date())
                .await()
            
            val updatedMatchDoc = matchesCollection.document(matchId).get().await()
            val updatedMatch = updatedMatchDoc.toObject(Match::class.java)
            
            if (updatedMatch != null) {
                Result.Success(updatedMatch)
            } else {
                Result.Error("Failed to parse updated match data")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating match status")
            Result.Error("Failed to update match status: ${e.message}")
        }
    }
    
    /**
     * Calculate distance between two coordinates in kilometers using Haversine formula
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val earthRadius = 6371 // Earth's radius in kilometers
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return (earthRadius * c).toInt()
    }
}