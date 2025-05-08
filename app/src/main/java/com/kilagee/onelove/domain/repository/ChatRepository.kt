package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Chat
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for chat operations
 */
interface ChatRepository {
    /**
     * Get all chats for the current user
     * @return Flow of Result containing a list of chats or an error
     */
    fun getAllChats(): Flow<Result<List<Chat>>>
    
    /**
     * Get a specific chat by ID
     * @param chatId ID of the chat to get
     * @return Flow of Result containing the chat or an error
     */
    fun getChatById(chatId: String): Flow<Result<Chat?>>
    
    /**
     * Get all messages for a specific chat
     * @param chatId ID of the chat to get messages for
     * @return Flow of Result containing a list of messages or an error
     */
    fun getMessages(chatId: String): Flow<Result<List<Message>>>
    
    /**
     * Send a message in a chat
     * @param chatId ID of the chat to send the message in
     * @param content Content of the message
     * @param attachmentUrls Optional list of attachment URLs
     * @return Result containing the sent message or an error
     */
    suspend fun sendMessage(
        chatId: String, 
        content: String,
        attachmentUrls: List<String> = emptyList()
    ): Result<Message>
    
    /**
     * Create a new chat with another user
     * @param recipientId ID of the user to chat with
     * @return Result containing the created chat or an error
     */
    suspend fun createChat(recipientId: String): Result<Chat>
    
    /**
     * Mark all messages in a chat as read
     * @param chatId ID of the chat to mark as read
     * @return Result indicating success or failure
     */
    suspend fun markChatAsRead(chatId: String): Result<Unit>
    
    /**
     * Delete a message
     * @param chatId ID of the chat containing the message
     * @param messageId ID of the message to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit>
    
    /**
     * Delete a chat and all its messages
     * @param chatId ID of the chat to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteChat(chatId: String): Result<Unit>
    
    /**
     * Get the total count of unread messages across all chats
     * @return Flow of Result containing the unread message count or an error
     */
    fun getUnreadMessageCount(): Flow<Result<Int>>
    
    /**
     * Send a typing indicator to a chat
     * @param chatId ID of the chat
     * @param isTyping Whether the user is typing
     * @return Result indicating success or failure
     */
    suspend fun sendTypingIndicator(chatId: String, isTyping: Boolean): Result<Unit>
    
    /**
     * Get the typing status of other users in a chat
     * @param chatId ID of the chat
     * @return Flow of Result containing a map of user IDs to typing status
     */
    fun getTypingStatus(chatId: String): Flow<Result<Map<String, Boolean>>>
    
    /**
     * Send a reaction to a message
     * @param chatId ID of the chat containing the message
     * @param messageId ID of the message to react to
     * @param reaction The reaction emoji
     * @return Result indicating success or failure
     */
    suspend fun sendReaction(chatId: String, messageId: String, reaction: String): Result<Unit>
    
    /**
     * Remove a reaction from a message
     * @param chatId ID of the chat containing the message
     * @param messageId ID of the message to remove reaction from
     * @param reaction The reaction emoji to remove
     * @return Result indicating success or failure
     */
    suspend fun removeReaction(chatId: String, messageId: String, reaction: String): Result<Unit>
}