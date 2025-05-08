package com.kilagee.onelove.domain.repository

import androidx.paging.PagingData
import com.kilagee.onelove.data.model.Chat
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.data.model.MessageType
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Repository interface for chat-related operations
 */
interface ChatRepository {
    
    /**
     * Get all chats for the current user
     * 
     * @return Flow of a list of chats
     */
    fun getChats(): Flow<Result<List<Chat>>>
    
    /**
     * Get a specific chat by ID
     * 
     * @param chatId ID of the chat
     * @return Flow of the chat
     */
    fun getChatById(chatId: String): Flow<Result<Chat>>
    
    /**
     * Get chat for a specific user or users
     * 
     * @param userIds List of user IDs
     * @return Flow of the chat or null if it doesn't exist
     */
    fun getChatForUsers(userIds: List<String>): Flow<Result<Chat?>>
    
    /**
     * Create a new chat
     * 
     * @param userIds List of user IDs to include in the chat
     * @param isGroupChat Whether this is a group chat
     * @param name Name of the group chat (only for group chats)
     * @return Result of the created chat
     */
    suspend fun createChat(
        userIds: List<String>,
        isGroupChat: Boolean = false,
        name: String? = null
    ): Result<Chat>
    
    /**
     * Get messages for a specific chat with pagination
     * 
     * @param chatId ID of the chat
     * @return Flow of paged messages
     */
    fun getMessages(chatId: String): Flow<PagingData<Message>>
    
    /**
     * Get recent messages for a specific chat
     * 
     * @param chatId ID of the chat
     * @param limit Maximum number of messages to retrieve
     * @return Flow of a list of messages
     */
    fun getRecentMessages(chatId: String, limit: Int = 20): Flow<Result<List<Message>>>
    
    /**
     * Send a text message
     * 
     * @param chatId ID of the chat
     * @param content Text content of the message
     * @return Result of the sent message
     */
    suspend fun sendTextMessage(chatId: String, content: String): Result<Message>
    
    /**
     * Send a media message
     * 
     * @param chatId ID of the chat
     * @param file Media file to send
     * @param type Type of media message
     * @param caption Optional caption for the media
     * @return Result of the sent message
     */
    suspend fun sendMediaMessage(
        chatId: String,
        file: File,
        type: MessageType,
        caption: String? = null
    ): Result<Message>
    
    /**
     * Mark all messages in a chat as read
     * 
     * @param chatId ID of the chat
     * @return Result of the operation
     */
    suspend fun markChatAsRead(chatId: String): Result<Unit>
    
    /**
     * Update typing status
     * 
     * @param chatId ID of the chat
     * @param isTyping Whether the user is typing
     * @return Result of the operation
     */
    suspend fun updateTypingStatus(chatId: String, isTyping: Boolean): Result<Unit>
    
    /**
     * Set chat pin status
     * 
     * @param chatId ID of the chat
     * @param isPinned Whether the chat should be pinned
     * @return Result of the operation
     */
    suspend fun setPinStatus(chatId: String, isPinned: Boolean): Result<Unit>
    
    /**
     * Set chat mute status
     * 
     * @param chatId ID of the chat
     * @param isMuted Whether the chat should be muted
     * @return Result of the operation
     */
    suspend fun setMuteStatus(chatId: String, isMuted: Boolean): Result<Unit>
    
    /**
     * Add a reaction to a message
     * 
     * @param messageId ID of the message
     * @param reaction Reaction to add (emoji)
     * @return Result of the operation
     */
    suspend fun addReaction(messageId: String, reaction: String): Result<Unit>
    
    /**
     * Remove a reaction from a message
     * 
     * @param messageId ID of the message
     * @return Result of the operation
     */
    suspend fun removeReaction(messageId: String): Result<Unit>
    
    /**
     * Get the total number of unread messages across all chats
     * 
     * @return Flow of the unread count
     */
    fun getTotalUnreadCount(): Flow<Int>
    
    /**
     * Search for messages by content
     * 
     * @param query Search query
     * @param chatId Optional chat ID to limit search to a specific chat
     * @return Flow of list of matching messages
     */
    fun searchMessages(query: String, chatId: String? = null): Flow<Result<List<Message>>>
    
    /**
     * Delete a message
     * 
     * @param messageId ID of the message to delete
     * @param deleteForEveryone Whether to delete the message for all users
     * @return Result of the operation
     */
    suspend fun deleteMessage(messageId: String, deleteForEveryone: Boolean = false): Result<Unit>
}