package com.kilagee.onelove.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kilagee.onelove.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Match entity operations
 */
@Dao
interface MatchDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)
    
    @Update
    suspend fun updateMatch(match: MatchEntity)
    
    @Delete
    suspend fun deleteMatch(match: MatchEntity)
    
    @Query("DELETE FROM matches WHERE id = :matchId")
    suspend fun deleteMatchById(matchId: String)
    
    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): MatchEntity?
    
    @Query("SELECT * FROM matches WHERE id = :matchId")
    fun getMatchByIdFlow(matchId: String): Flow<MatchEntity?>
    
    @Query("SELECT * FROM matches WHERE userId1 = :userId OR userId2 = :userId ORDER BY createdAt DESC")
    suspend fun getMatchesForUser(userId: String): List<MatchEntity>
    
    @Query("SELECT * FROM matches WHERE userId1 = :userId OR userId2 = :userId ORDER BY createdAt DESC")
    fun getMatchesForUserFlow(userId: String): Flow<List<MatchEntity>>
    
    @Query("SELECT * FROM matches WHERE (userId1 = :userId OR userId2 = :userId) AND status = 'MATCHED' ORDER BY matchedAt DESC")
    suspend fun getActiveMatchesForUser(userId: String): List<MatchEntity>
    
    @Query("SELECT * FROM matches WHERE (userId1 = :userId OR userId2 = :userId) AND status = 'PENDING' ORDER BY createdAt DESC")
    suspend fun getPendingMatchesForUser(userId: String): List<MatchEntity>
    
    @Query("SELECT * FROM matches WHERE (userId1 = :userId1 AND userId2 = :userId2) OR (userId1 = :userId2 AND userId2 = :userId1) LIMIT 1")
    suspend fun getMatchBetweenUsers(userId1: String, userId2: String): MatchEntity?
    
    @Query("SELECT * FROM matches WHERE chatId = :chatId")
    suspend fun getMatchByChatId(chatId: String): MatchEntity?
    
    @Query("UPDATE matches SET status = :status WHERE id = :matchId")
    suspend fun updateMatchStatus(matchId: String, status: String)
    
    @Query("UPDATE matches SET user1LikedUser2 = :liked WHERE id = :matchId")
    suspend fun updateUser1Liked(matchId: String, liked: Boolean)
    
    @Query("UPDATE matches SET user2LikedUser1 = :liked WHERE id = :matchId")
    suspend fun updateUser2Liked(matchId: String, liked: Boolean)
    
    @Query("UPDATE matches SET user1RejectedUser2 = :rejected WHERE id = :matchId")
    suspend fun updateUser1Rejected(matchId: String, rejected: Boolean)
    
    @Query("UPDATE matches SET user2RejectedUser1 = :rejected WHERE id = :matchId")
    suspend fun updateUser2Rejected(matchId: String, rejected: Boolean)
    
    @Query("UPDATE matches SET chatId = :chatId WHERE id = :matchId")
    suspend fun updateMatchChatId(matchId: String, chatId: String)
    
    @Query("UPDATE matches SET hasActiveOffers = :hasActiveOffers WHERE id = :matchId")
    suspend fun updateMatchHasActiveOffers(matchId: String, hasActiveOffers: Boolean)
    
    @Query("UPDATE matches SET lastMessageTime = :lastMessageTime WHERE id = :matchId")
    suspend fun updateMatchLastMessageTime(matchId: String, lastMessageTime: Long)
    
    @Query("UPDATE matches SET matchedAt = :matchedAt WHERE id = :matchId")
    suspend fun updateMatchedAt(matchId: String, matchedAt: Long)
    
    @Query("UPDATE matches SET unmatchedAt = :unmatchedAt WHERE id = :matchId")
    suspend fun updateUnmatchedAt(matchId: String, unmatchedAt: Long)
    
    @Query("UPDATE matches SET user1Notified = :notified WHERE id = :matchId")
    suspend fun updateUser1Notified(matchId: String, notified: Boolean)
    
    @Query("UPDATE matches SET user2Notified = :notified WHERE id = :matchId")
    suspend fun updateUser2Notified(matchId: String, notified: Boolean)
    
    @Query("SELECT COUNT(*) FROM matches WHERE (userId1 = :userId OR userId2 = :userId) AND status = 'MATCHED'")
    suspend fun getActiveMatchCount(userId: String): Int
}