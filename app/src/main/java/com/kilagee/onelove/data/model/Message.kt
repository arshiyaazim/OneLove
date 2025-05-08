package com.kilagee.onelove.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Message types
 */
enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    LOCATION,
    CONTACT,
    SYSTEM,
    CALL_REQUEST,
    GIFT,
    STICKER,
    AI_GENERATED
}

/**
 * Message status
 */
enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

/**
 * Message entity
 */
@Entity(tableName = "messages")
@Parcelize
data class Message(
    @PrimaryKey
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    
    /**
     * ID of the chat this message belongs to
     */
    val chatId: String = "",
    
    /**
     * ID of the user who sent the message
     */
    val senderId: String = "",
    
    /**
     * Type of message
     */
    val type: MessageType = MessageType.TEXT,
    
    /**
     * Text content of the message
     */
    val content: String = "",
    
    /**
     * URL of the media (if applicable)
     */
    val mediaUrl: String? = null,
    
    /**
     * Media type (MIME type) if applicable
     */
    val mediaType: String? = null,
    
    /**
     * Media size in bytes (if applicable)
     */
    val mediaSize: Long? = null,
    
    /**
     * Media width in pixels (if applicable)
     */
    val mediaWidth: Int? = null,
    
    /**
     * Media height in pixels (if applicable)
     */
    val mediaHeight: Int? = null,
    
    /**
     * Media duration in seconds (if applicable)
     */
    val mediaDuration: Int? = null,
    
    /**
     * Media thumbnail URL (if applicable)
     */
    val thumbnailUrl: String? = null,
    
    /**
     * Status of the message
     */
    val status: MessageStatus = MessageStatus.SENT,
    
    /**
     * Map of user reactions to the message
     * Key: User ID, Value: Reaction (emoji)
     */
    val reactions: Map<String, String> = emptyMap(),
    
    /**
     * Whether the message has been edited
     */
    val isEdited: Boolean = false,
    
    /**
     * IDs of users who have read the message
     */
    val readBy: List<String> = emptyList(),
    
    /**
     * Reply to another message (ID of the message being replied to)
     */
    val replyTo: String? = null,
    
    /**
     * Custom metadata for the message
     */
    val metadata: Map<String, String> = emptyMap(),
    
    /**
     * Timestamp when the message was created
     */
    val createdAt: Timestamp = Timestamp.now(),
    
    /**
     * Timestamp when the message was last updated
     */
    val updatedAt: Timestamp = Timestamp.now(),
    
    /**
     * Timestamp when the message was deleted (if applicable)
     */
    val deletedAt: Timestamp? = null
) : Parcelable {
    
    /**
     * Check if the message is from the specified user
     * 
     * @param userId ID of the user
     * @return True if the message is from the specified user
     */
    fun isFromUser(userId: String): Boolean {
        return senderId == userId
    }
    
    /**
     * Check if the message has media
     * 
     * @return True if the message has media
     */
    fun hasMedia(): Boolean {
        return mediaUrl != null && type in listOf(
            MessageType.IMAGE,
            MessageType.VIDEO,
            MessageType.AUDIO,
            MessageType.FILE
        )
    }
    
    /**
     * Check if the message has been read by a specific user
     * 
     * @param userId ID of the user
     * @return True if the message has been read by the user
     */
    fun isReadBy(userId: String): Boolean {
        return readBy.contains(userId)
    }
    
    /**
     * Check if the message has been deleted
     * 
     * @return True if the message has been deleted
     */
    fun isDeleted(): Boolean {
        return deletedAt != null
    }
    
    /**
     * Get the number of reactions
     * 
     * @return Number of reactions
     */
    fun getReactionCount(): Int {
        return reactions.size
    }
    
    /**
     * Get the reaction from a specific user
     * 
     * @param userId ID of the user
     * @return Reaction emoji or null if the user hasn't reacted
     */
    fun getReactionFrom(userId: String): String? {
        return reactions[userId]
    }
    
    /**
     * Format media size to a human-readable string
     * 
     * @return Formatted size (e.g., "2.5 MB") or null if no media
     */
    fun getFormattedMediaSize(): String? {
        val size = mediaSize ?: return null
        
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "%.1f KB".format(size / 1024.0)
            size < 1024 * 1024 * 1024 -> "%.1f MB".format(size / (1024.0 * 1024.0))
            else -> "%.1f GB".format(size / (1024.0 * 1024.0 * 1024.0))
        }
    }
    
    /**
     * Format media duration to a human-readable string
     * 
     * @return Formatted duration (e.g., "2:30") or null if no duration
     */
    fun getFormattedMediaDuration(): String? {
        val duration = mediaDuration ?: return null
        
        val minutes = duration / 60
        val seconds = duration % 60
        
        return "%d:%02d".format(minutes, seconds)
    }
}