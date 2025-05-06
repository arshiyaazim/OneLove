package com.kilagee.onelove.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kilagee.onelove.data.local.entity.UserEntity
import com.kilagee.onelove.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for User entity operations
 */
@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Query("SELECT * FROM users ORDER BY displayName")
    suspend fun getAllUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users ORDER BY displayName")
    fun getAllUsersFlow(): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users WHERE displayName LIKE :searchQuery OR email LIKE :searchQuery ORDER BY displayName LIMIT 20")
    suspend fun searchUsers(searchQuery: String): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE isOnline = 1 ORDER BY lastActive DESC")
    suspend fun getOnlineUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE verificationStatus = :status")
    suspend fun getUsersByVerificationStatus(status: String): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE verificationStatus = 'VERIFIED'")
    suspend fun getVerifiedUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE isAdmin = 1")
    suspend fun getAdminUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE isPremium = 1")
    suspend fun getPremiumUsers(): List<UserEntity>
    
    @Query("UPDATE users SET isOnline = :isOnline WHERE id = :userId")
    suspend fun updateUserOnlineStatus(userId: String, isOnline: Boolean)
    
    @Query("UPDATE users SET lastActive = :timestamp WHERE id = :userId")
    suspend fun updateUserLastActive(userId: String, timestamp: Long)
    
    @Query("UPDATE users SET points = points + :points WHERE id = :userId")
    suspend fun addUserPoints(userId: String, points: Int)
    
    @Query("UPDATE users SET points = points - :points WHERE id = :userId AND points >= :points")
    suspend fun subtractUserPoints(userId: String, points: Int): Int
    
    @Query("UPDATE users SET isPremium = :isPremium WHERE id = :userId")
    suspend fun updateUserPremiumStatus(userId: String, isPremium: Boolean)
    
    @Transaction
    suspend fun updateUserVerificationStatus(userId: String, status: VerificationStatus) {
        updateUserVerificationStatusInternal(userId, status.name)
        if (status == VerificationStatus.VERIFIED) {
            updateUserIsVerified(userId, true)
        } else if (status == VerificationStatus.REJECTED || status == VerificationStatus.NOT_VERIFIED) {
            updateUserIsVerified(userId, false)
        }
    }
    
    @Query("UPDATE users SET verificationStatus = :status WHERE id = :userId")
    suspend fun updateUserVerificationStatusInternal(userId: String, status: String)
    
    @Query("UPDATE users SET isVerified = :isVerified WHERE id = :userId")
    suspend fun updateUserIsVerified(userId: String, isVerified: Boolean)
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Query("SELECT COUNT(*) FROM users WHERE isPremium = 1")
    suspend fun getPremiumUserCount(): Int
}