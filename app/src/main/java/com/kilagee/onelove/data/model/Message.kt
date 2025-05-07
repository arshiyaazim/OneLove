package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Data class representing a message
 */
@Parcelize
data class Message(
    val id: String,
    val matchId: String,
    val senderId: String,
    val text: String? = null,
    val mediaUrl: String? = null,
    val mediaType: MediaType? = null,
    val createdAt: Date,
    val readAt: Map<String, Date?> = emptyMap(), // userId -> readAt date
    val status: MessageStatus = MessageStatus.SENT,
    val reactions: Map<String, Reaction> = emptyMap(), // userId -> reaction
    val isDeleted: Boolean = false,
    val deletedFor: List<String> = emptyList(), // List of user IDs for whom this message is deleted
    val replyToMessageId: String? = null,
    val isAiGenerated: Boolean = false,
    val metadata: Map<String, String> = emptyMap()
) : Parcelable

/**
 * Data class representing a chat conversation
 */
@Parcelize
data class Conversation(
    val matchId: String,
    val participants: List<User>,
    val lastMessage: Message? = null,
    val unreadCount: Map<String, Int> = emptyMap(), // userId -> unread count
    val isActive: Boolean = true,
    val isPinned: Map<String, Boolean> = emptyMap(), // userId -> isPinned
    val createdAt: Date,
    val updatedAt: Date,
    val metaData: Map<String, String> = emptyMap()
) : Parcelable

/**
 * Enum representing message status
 */
@Parcelize
enum class MessageStatus : Parcelable {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

/**
 * Enum representing media type
 */
@Parcelize
enum class MediaType : Parcelable {
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    GIF,
    STICKER,
    LOCATION
}

/**
 * Data class representing a reaction to a message
 */
@Parcelize
data class Reaction(
    val userId: String,
    val type: String, // Can be an emoji or a predefined reaction type
    val createdAt: Date
) : Parcelable