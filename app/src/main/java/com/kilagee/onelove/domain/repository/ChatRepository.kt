package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.ChatMessage
import com.kilagee.onelove.data.model.ChatThread
import com.kilagee.onelove.data.model.MessageType
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Interface for chat-related operations
 */
interface ChatRepository {
    
    /**
     * Get all chat threads for the current user
     * @param limit Maximum number of threads to fetch
     * @return List of [ChatThread] objects
     */
    suspend fun getChatThreads(limit: Int = 20): Result<List<ChatThread>>
    
    /**
     * Get chat threads as a flow for real-time updates
     * @param limit Maximum number of threads to fetch initially
     * @return Flow of [ChatThread] lists
     */
    fun getChatThreadsFlow(limit: Int = 20): Flow<Result<List<ChatThread>>>
    
    /**
     * Get a specific chat thread
     * @param threadId ID of the thread to retrieve
     * @return The [ChatThread] object
     */
    suspend fun getChatThread(threadId: String): Result<ChatThread>
    
    /**
     * Create a new chat thread
     * @param participants List of user IDs participating in the chat
     * @param isGroup Whether this is a group chat
     * @param groupName Name of the group (for group chats)
     * @param groupImage URL of the group image (for group chats)
     * @return The created [ChatThread]
     */
    suspend fun createChatThread(
        participants: List<String>,
        isGroup: Boolean = false,
        groupName: String? = null,
        groupImage: String? = null
    ): Result<ChatThread>
    
    /**
     * Get chat messages for a thread
     * @param threadId ID of the thread
     * @param limit Maximum number of messages to fetch
     * @return List of [ChatMessage] objects
     */
    suspend fun getChatMessages(threadId: String, limit: Int = 50): Result<List<ChatMessage>>
    
    /**
     * Get chat messages as a flow for real-time updates
     * @param threadId ID of the thread
     * @param limit Maximum number of messages to fetch initially
     * @return Flow of [ChatMessage] lists
     */
    fun getChatMessagesFlow(threadId: String, limit: Int = 50): Flow<Result<List<ChatMessage>>>
    
    /**
     * Send a text message
     * @param text Message text
     * @param threadId ID of the thread
     * @param receiverId ID of the message recipient
     * @param replyToMessageId ID of the message being replied to (optional)
     * @return The sent [ChatMessage]
     */
    suspend fun sendTextMessage(
        text: String,
        threadId: String,
        receiverId: String,
        replyToMessageId: String? = null
    ): Result<ChatMessage>
    
    /**
     * Send a message (any type)
     * @param message The [ChatMessage] to send
     * @return The sent [ChatMessage]
     */
    suspend fun sendMessage(message: ChatMessage): Result<ChatMessage>
    
    /**
     * Send an image message
     * @param imageFile Image file to send
     * @param threadId ID of the thread
     * @param receiverId ID of the message recipient
     * @param caption Optional caption for the image
     * @return The sent [ChatMessage]
     */
    suspend fun sendImageMessage(
        imageFile: File,
        threadId: String,
        receiverId: String,
        caption: String? = null
    ): Result<ChatMessage>
    
    /**
     * Send a file message
     * @param file File to send
     * @param threadId ID of the thread
     * @param receiverId ID of the message recipient
     * @param caption Optional caption for the file
     * @return The sent [ChatMessage]
     */
    suspend fun sendFileMessage(
        file: File,
        threadId: String,
        receiverId: String,
        caption: String? = null
    ): Result<ChatMessage>
    
    /**
     * Send a location message
     * @param latitude Latitude
     * @param longitude Longitude
     * @param address Optional address string
     * @param threadId ID of the thread
     * @param receiverId ID of the message recipient
     * @return The sent [ChatMessage]
     */
    suspend fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        address: String? = null,
        threadId: String,
        receiverId: String
    ): Result<ChatMessage>
    
    /**
     * Delete a message
     * @param messageId ID of the message to delete
     * @param threadId ID of the thread
     * @param forEveryone Whether to delete for all participants
     * @return Success result
     */
    suspend fun deleteMessage(
        messageId: String,
        threadId: String,
        forEveryone: Boolean = false
    ): Result<Unit>
    
    /**
     * Mark messages as read
     * @param threadId ID of the thread
     * @param messageIds List of message IDs to mark as read
     * @return Success result
     */
    suspend fun markMessagesAsRead(threadId: String, messageIds: List<String>): Result<Unit>
    
    /**
     * Mark all messages in a thread as read
     * @param threadId ID of the thread
     * @return Success result
     */
    suspend fun markAllMessagesAsRead(threadId: String): Result<Unit>
    
    /**
     * Mark messages as delivered
     * @param threadId ID of the thread
     * @return Success result
     */
    suspend fun markMessagesAsDelivered(threadId: String): Result<Unit>
    
    /**
     * Set typing status
     * @param threadId ID of the thread
     * @param isTyping Whether the user is typing
     * @return Success result
     */
    suspend fun setTypingStatus(threadId: String, isTyping: Boolean): Result<Unit>
    
    /**
     * Get typing users in a thread
     * @param threadId ID of the thread
     * @return List of user IDs who are currently typing
     */
    suspend fun getTypingUsers(threadId: String): Result<List<String>>
    
    /**
     * Get typing users as a flow for real-time updates
     * @param threadId ID of the thread
     * @return Flow of typing user ID lists
     */
    fun getTypingUsersFlow(threadId: String): Flow<Result<List<String>>>
    
    /**
     * Get unread messages count
     * @return Number of unread messages across all threads
     */
    suspend fun getUnreadMessagesCount(): Result<Int>
    
    /**
     * Get unread messages count as a flow for real-time updates
     * @return Flow of unread message counts
     */
    fun getUnreadMessagesCountFlow(): Flow<Result<Int>>
    
    /**
     * Mute or unmute a chat thread
     * @param threadId ID of the thread
     * @param muted Whether to mute or unmute
     * @return Success result
     */
    suspend fun muteChat(threadId: String, muted: Boolean): Result<Unit>
    
    /**
     * Block or unblock a user in a chat
     * @param threadId ID of the thread
     * @param userId ID of the user to block
     * @param blocked Whether to block or unblock
     * @return Success result
     */
    suspend fun blockUserInChat(threadId: String, userId: String, blocked: Boolean): Result<Unit>
    
    /**
     * Upload an attachment (image, video, file, etc.)
     * @param file File to upload
     * @param type Type of attachment
     * @return URL of the uploaded file
     */
    suspend fun uploadAttachment(file: File, type: MessageType): Result<String>
    
    /**
     * Download an attachment
     * @param url URL of the attachment
     * @param destinationFile File to save the attachment to
     * @return Success result
     */
    suspend fun downloadAttachment(url: String, destinationFile: File): Result<Unit>
}