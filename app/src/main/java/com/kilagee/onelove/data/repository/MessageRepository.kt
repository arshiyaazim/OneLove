package com.kilagee.onelove.data.repository

import android.net.Uri
import com.kilagee.onelove.data.model.Chat
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.data.model.MessageType
import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for messages and chats
 */
interface MessageRepository {
    
    /**
     * Get message by ID
     * @param messageId Message ID
     * @return Result containing the message or error
     */
    suspend fun getMessageById(messageId: String): Result<Message>
    
    /**
     * Get chat by ID
     * @param chatId Chat ID
     * @return Result containing the chat or error
     */
    suspend fun getChatById(chatId: String): Result<Chat>
    
    /**
     * Get chat by ID as Flow
     * @param chatId Chat ID
     * @return Flow emitting Result containing the chat or error
     */
    fun getChatByIdFlow(chatId: String): Flow<Result<Chat>>
    
    /**
     * Get or create chat between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Result containing the chat or error
     */
    suspend fun getOrCreateChatBetweenUsers(userId1: String, userId2: String): Result<Chat>
    
    /**
     * Get all chats for user
     * @param userId User ID
     * @return Result containing list of chats or error
     */
    suspend fun getChatsForUser(userId: String): Result<List<Chat>>
    
    /**
     * Get all chats for user as Flow
     * @param userId User ID
     * @return Flow emitting Result containing list of chats or error
     */
    fun getChatsForUserFlow(userId: String): Flow<Result<List<Chat>>>
    
    /**
     * Get messages in chat
     * @param chatId Chat ID
     * @param limit Maximum number of messages
     * @return Result containing list of messages or error
     */
    suspend fun getMessagesInChat(chatId: String, limit: Int = 50): Result<List<Message>>
    
    /**
     * Get messages in chat as Flow
     * @param chatId Chat ID
     * @return Flow emitting Result containing list of messages or error
     */
    fun getMessagesInChatFlow(chatId: String): Flow<Result<List<Message>>>
    
    /**
     * Get unread messages for user
     * @param userId User ID
     * @return Result containing list of unread messages or error
     */
    suspend fun getUnreadMessagesForUser(userId: String): Result<List<Message>>
    
    /**
     * Get unread message count for user
     * @param userId User ID
     * @return Result containing unread count or error
     */
    suspend fun getUnreadMessageCount(userId: String): Result<Int>
    
    /**
     * Get unread message count for user as Flow
     * @param userId User ID
     * @return Flow emitting Result containing unread count or error
     */
    fun getUnreadMessageCountFlow(userId: String): Flow<Result<Int>>
    
    /**
     * Get unread message count in chat
     * @param chatId Chat ID
     * @param userId User ID
     * @return Result containing unread count or error
     */
    suspend fun getUnreadMessageCountInChat(chatId: String, userId: String): Result<Int>
    
    /**
     * Get unread message count in chat as Flow
     * @param chatId Chat ID
     * @param userId User ID
     * @return Flow emitting Result containing unread count or error
     */
    fun getUnreadMessageCountInChatFlow(chatId: String, userId: String): Flow<Result<Int>>
    
    /**
     * Send text message
     * @param chatId Chat ID
     * @param senderId Sender ID
     * @param receiverId Receiver ID
     * @param text Message text
     * @return Result containing the sent message or error
     */
    suspend fun sendTextMessage(
        chatId: String, 
        senderId: String,
        receiverId: String,
        text: String
    ): Result<Message>
    
    /**
     * Send media message
     * @param chatId Chat ID
     * @param senderId Sender ID
     * @param receiverId Receiver ID
     * @param mediaUri Media URI
     * @param messageType Message type (IMAGE, VIDEO, AUDIO)
     * @param caption Optional caption
     * @return Result containing the sent message or error
     */
    suspend fun sendMediaMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        mediaUri: Uri,
        messageType: MessageType,
        caption: String? = null
    ): Result<Message>
    
    /**
     * Mark message as read
     * @param messageId Message ID
     * @return Result indicating success or error
     */
    suspend fun markMessageAsRead(messageId: String): Result<Unit>
    
    /**
     * Mark all messages in chat as read
     * @param chatId Chat ID
     * @param userId User ID
     * @return Result indicating success or error
     */
    suspend fun markAllMessagesAsRead(chatId: String, userId: String): Result<Unit>
    
    /**
     * Delete message
     * @param messageId Message ID
     * @return Result indicating success or error
     */
    suspend fun deleteMessage(messageId: String): Result<Unit>
    
    /**
     * Delete chat and all messages
     * @param chatId Chat ID
     * @return Result indicating success or error
     */
    suspend fun deleteChat(chatId: String): Result<Unit>
    
    /**
     * Create group chat
     * @param name Group name
     * @param participantIds List of participant IDs
     * @param creatorId Creator ID
     * @param groupPhotoUri Optional group photo URI
     * @return Result containing the created chat or error
     */
    suspend fun createGroupChat(
        name: String,
        participantIds: List<String>,
        creatorId: String,
        groupPhotoUri: Uri? = null
    ): Result<Chat>
    
    /**
     * Add user to group chat
     * @param chatId Chat ID
     * @param userId User ID
     * @return Result indicating success or error
     */
    suspend fun addUserToGroupChat(chatId: String, userId: String): Result<Unit>
    
    /**
     * Remove user from group chat
     * @param chatId Chat ID
     * @param userId User ID
     * @return Result indicating success or error
     */
    suspend fun removeUserFromGroupChat(chatId: String, userId: String): Result<Unit>
    
    /**
     * Update group chat name
     * @param chatId Chat ID
     * @param name New name
     * @return Result indicating success or error
     */
    suspend fun updateGroupChatName(chatId: String, name: String): Result<Unit>
    
    /**
     * Update group chat photo
     * @param chatId Chat ID
     * @param photoUri Photo URI
     * @return Result containing the photo URL or error
     */
    suspend fun updateGroupChatPhoto(chatId: String, photoUri: Uri): Result<String>
    
    /**
     * Get chat participants
     * @param chatId Chat ID
     * @return Result containing list of users or error
     */
    suspend fun getChatParticipants(chatId: String): Result<List<User>>
    
    /**
     * Get chat participants as Flow
     * @param chatId Chat ID
     * @return Flow emitting Result containing list of users or error
     */
    fun getChatParticipantsFlow(chatId: String): Flow<Result<List<User>>>
    
    /**
     * Get last message in chat
     * @param chatId Chat ID
     * @return Result containing the message or error
     */
    suspend fun getLastMessage(chatId: String): Result<Message>
    
    /**
     * Send system message to chat
     * @param chatId Chat ID
     * @param text System message text
     * @return Result containing the sent message or error
     */
    suspend fun sendSystemMessage(chatId: String, text: String): Result<Message>
    
    /**
     * Reply to message
     * @param chatId Chat ID
     * @param senderId Sender ID
     * @param receiverId Receiver ID
     * @param text Reply text
     * @param replyToMessageId Message being replied to
     * @return Result containing the sent message or error
     */
    suspend fun replyToMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        text: String,
        replyToMessageId: String
    ): Result<Message>
    
    /**
     * Search messages in chat
     * @param chatId Chat ID
     * @param query Search query
     * @return Result containing list of messages or error
     */
    suspend fun searchMessagesInChat(chatId: String, query: String): Result<List<Message>>
}