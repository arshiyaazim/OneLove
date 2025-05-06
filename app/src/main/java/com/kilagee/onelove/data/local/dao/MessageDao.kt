package com.kilagee.onelove.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kilagee.onelove.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Message entity operations
 */
@Dao
interface MessageDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)
    
    @Update
    suspend fun updateMessage(message: MessageEntity)
    
    @Delete
    suspend fun deleteMessage(message: MessageEntity)
    
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String)
    
    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesByChatId(chatId: String)
    
    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC")
    suspend fun getMessagesByChatId(chatId: String): List<MessageEntity>
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC")
    fun getMessagesByChatIdFlow(chatId: String): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM messages WHERE senderId = :senderId OR receiverId = :receiverId ORDER BY createdAt DESC")
    suspend fun getMessagesForUser(senderId: String, receiverId: String): List<MessageEntity>
    
    @Query("SELECT * FROM messages WHERE receiverId = :userId AND isRead = 0 AND isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getUnreadMessagesForUser(userId: String): List<MessageEntity>
    
    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0 AND isDeleted = 0")
    suspend fun getUnreadMessageCountForUser(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId AND receiverId = :userId AND isRead = 0 AND isDeleted = 0")
    suspend fun getUnreadMessageCountForChat(chatId: String, userId: String): Int
    
    @Query("UPDATE messages SET isRead = 1, readAt = :readAt WHERE id = :messageId")
    suspend fun markMessageAsRead(messageId: String, readAt: Long)
    
    @Query("UPDATE messages SET isRead = 1, readAt = :readAt WHERE chatId = :chatId AND receiverId = :userId AND isRead = 0")
    suspend fun markChatMessagesAsRead(chatId: String, userId: String, readAt: Long)
    
    @Query("UPDATE messages SET isDelivered = 1, deliveredAt = :deliveredAt WHERE id = :messageId")
    suspend fun markMessageAsDelivered(messageId: String, deliveredAt: Long)
    
    @Query("UPDATE messages SET isSending = 0 WHERE id = :messageId")
    suspend fun markMessageAsSent(messageId: String)
    
    @Query("UPDATE messages SET isDeleted = 1 WHERE id = :messageId")
    suspend fun markMessageAsDeleted(messageId: String)
    
    @Query("SELECT * FROM messages WHERE messageType = :messageType ORDER BY createdAt DESC")
    suspend fun getMessagesByType(messageType: String): List<MessageEntity>
    
    @Query("SELECT * FROM messages WHERE isAI = 1 ORDER BY createdAt DESC")
    suspend fun getAIMessages(): List<MessageEntity>
    
    @Query("SELECT COUNT(*) FROM messages")
    suspend fun getMessageCount(): Int
    
    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId")
    suspend fun getMessageCountForChat(chatId: String): Int
    
    @Query("SELECT * FROM messages WHERE content LIKE :searchQuery OR senderName LIKE :searchQuery ORDER BY createdAt DESC")
    suspend fun searchMessages(searchQuery: String): List<MessageEntity>
}