package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Conversation
import com.kilagee.onelove.data.model.MediaType
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Interface for chat operations
 */
interface ChatRepository {
    
    /**
     * Get all conversations for the current user
     * @return List of [Conversation] objects
     */
    suspend fun getConversations(): Result<List<Conversation>>
    
    /**
     * Get conversations as a flow for real-time updates
     * @return Flow of [Conversation] lists
     */
    fun getConversationsFlow(): Flow<Result<List<Conversation>>>
    
    /**
     * Get a specific conversation by match ID
     * @param matchId ID of the match/conversation
     * @return The [Conversation] object
     */
    suspend fun getConversation(matchId: String): Result<Conversation>
    
    /**
     * Get conversation as a flow for real-time updates
     * @param matchId ID of the match/conversation
     * @return Flow of the [Conversation]
     */
    fun getConversationFlow(matchId: String): Flow<Result<Conversation>>
    
    /**
     * Get messages for a conversation
     * @param matchId ID of the match/conversation
     * @param limit Maximum number of messages to fetch
     * @param before Message ID to fetch messages before (for pagination)
     * @return List of [Message] objects
     */
    suspend fun getMessages(
        matchId: String, 
        limit: Int = 20, 
        before: String? = null
    ): Result<List<Message>>
    
    /**
     * Get messages as a flow for real-time updates
     * @param matchId ID of the match/conversation
     * @param limit Maximum number of messages to fetch initially
     * @return Flow of [Message] lists
     */
    fun getMessagesFlow(matchId: String, limit: Int = 20): Flow<Result<List<Message>>>
    
    /**
     * Send a text message
     * @param matchId ID of the match/conversation
     * @param text Text content of the message
     * @param replyToMessageId Optional ID of the message being replied to
     * @return The sent [Message]
     */
    suspend fun sendTextMessage(
        matchId: String, 
        text: String, 
        replyToMessageId: String? = null
    ): Result<Message>
    
    /**
     * Send a media message
     * @param matchId ID of the match/conversation
     * @param file File to upload
     * @param mediaType Type of media being sent
     * @param text Optional text to accompany the media
     * @param replyToMessageId Optional ID of the message being replied to
     * @return The sent [Message]
     */
    suspend fun sendMediaMessage(
        matchId: String, 
        file: File,
        mediaType: MediaType,
        text: String? = null,
        replyToMessageId: String? = null
    ): Result<Message>
    
    /**
     * Mark messages as read
     * @param matchId ID of the match/conversation
     * @param messageIds List of message IDs to mark as read
     */
    suspend fun markMessagesAsRead(matchId: String, messageIds: List<String>): Result<Unit>
    
    /**
     * Delete a message
     * @param messageId ID of the message to delete
     * @param forEveryone Whether to delete for everyone or just the current user
     */
    suspend fun deleteMessage(messageId: String, forEveryone: Boolean = false): Result<Unit>
    
    /**
     * React to a message
     * @param messageId ID of the message to react to
     * @param reactionType Type of reaction (emoji or predefined type)
     */
    suspend fun reactToMessage(messageId: String, reactionType: String): Result<Unit>
    
    /**
     * Remove a reaction from a message
     * @param messageId ID of the message to remove reaction from
     */
    suspend fun removeReaction(messageId: String): Result<Unit>
    
    /**
     * Get suggested replies for a message
     * @param matchId ID of the match/conversation
     * @param messageId ID of the last message to generate suggestions for
     * @return List of suggested reply texts
     */
    suspend fun getSuggestedReplies(matchId: String, messageId: String): Result<List<String>>
    
    /**
     * Pin a conversation
     * @param matchId ID of the match/conversation to pin
     */
    suspend fun pinConversation(matchId: String): Result<Unit>
    
    /**
     * Unpin a conversation
     * @param matchId ID of the match/conversation to unpin
     */
    suspend fun unpinConversation(matchId: String): Result<Unit>
    
    /**
     * Archive a conversation
     * @param matchId ID of the match/conversation to archive
     */
    suspend fun archiveConversation(matchId: String): Result<Unit>
    
    /**
     * Unarchive a conversation
     * @param matchId ID of the match/conversation to unarchive
     */
    suspend fun unarchiveConversation(matchId: String): Result<Unit>
    
    /**
     * Clear conversation history
     * @param matchId ID of the match/conversation to clear
     */
    suspend fun clearConversation(matchId: String): Result<Unit>
    
    /**
     * Search for messages
     * @param query Search query
     * @param matchId Optional match ID to limit search to a specific conversation
     * @return List of [Message] objects matching the query
     */
    suspend fun searchMessages(query: String, matchId: String? = null): Result<List<Message>>
    
    /**
     * Block a user and end the conversation
     * @param userId ID of the user to block
     */
    suspend fun blockUser(userId: String): Result<Unit>
    
    /**
     * Report a user
     * @param userId ID of the user to report
     * @param reason Reason for reporting
     * @param messageIds Optional list of message IDs as evidence
     */
    suspend fun reportUser(
        userId: String, 
        reason: String, 
        messageIds: List<String>? = null
    ): Result<Unit>
}