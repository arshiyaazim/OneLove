package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Chat
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for chat functionality
 */
interface ChatRepository {
    
    // Chat methods
    fun getChatsForCurrentUser(): Flow<Resource<List<Chat>>>
    
    fun getChatById(chatId: String): Flow<Resource<Chat>>
    
    fun getChatBetweenUsers(user1Id: String, user2Id: String): Flow<Resource<Chat?>>
    
    fun createChat(user1Id: String, user2Id: String): Flow<Resource<Chat>>
    
    fun updateLastMessage(chatId: String, messageId: String, lastMessageText: String): Flow<Resource<Unit>>
    
    fun resetUnreadCount(chatId: String): Flow<Resource<Unit>>
    
    fun blockChat(chatId: String, isBlocked: Boolean): Flow<Resource<Unit>>
    
    fun muteChat(chatId: String, isMuted: Boolean): Flow<Resource<Unit>>
    
    // Message methods
    fun getMessagesByChatId(chatId: String): Flow<Resource<List<Message>>>
    
    fun getMessageById(messageId: String): Flow<Resource<Message>>
    
    fun sendMessage(message: Message): Flow<Resource<Message>>
    
    fun markMessageAsRead(messageId: String): Flow<Resource<Unit>>
    
    fun markMessageAsDelivered(messageId: String): Flow<Resource<Unit>>
    
    fun deleteMessage(messageId: String): Flow<Resource<Unit>>
    
    fun getUnreadMessagesCount(): Flow<Resource<Int>>
}