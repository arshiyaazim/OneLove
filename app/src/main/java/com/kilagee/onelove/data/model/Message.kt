package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Message model representing a message in a conversation
 */
@Parcelize
data class Message(
    val id: String = "",
    val matchId: String = "",
    val senderId: String = "",
    val recipientId: String = "",
    val text: String = "",
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String = "",
    val thumbnailUrl: String = "",
    val mediaDuration: Long? = null, // Duration in milliseconds for audio/video
    val mediaWidth: Int? = null, // Width for image/video
    val mediaHeight: Int? = null, // Height for image/video
    val mediaSize: Long? = null, // Size in bytes
    val mediaType: String = "", // MIME type
    val status: MessageStatus = MessageStatus.SENDING,
    val readAt: Date? = null,
    val deliveredAt: Date? = null,
    val reactions: List<MessageReaction> = emptyList(),
    val replyToMessageId: String? = null,
    val isEdited: Boolean = false,
    val editedAt: Date? = null,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Date = Date(),
    val isFromAI: Boolean = false,
    val aiProfileId: String = "",
    val offerId: String = "", // If this message is about an offer
    val isSystemMessage: Boolean = false
) : Parcelable

/**
 * Message type enum
 */
@Parcelize
enum class MessageType : Parcelable {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    LOCATION,
    CONTACT,
    STICKER,
    GIF,
    FILE,
    OFFER,
    SYSTEM
}

/**
 * Message status enum
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
 * Message reaction model
 */
@Parcelize
data class MessageReaction(
    val userId: String,
    val reaction: String,
    val createdAt: Date = Date()
) : Parcelable

/**
 * Message draft model
 */
@Parcelize
data class MessageDraft(
    val matchId: String,
    val text: String,
    val attachments: List<MessageAttachment> = emptyList(),
    val replyToMessageId: String? = null,
    val updatedAt: Date = Date()
) : Parcelable

/**
 * Message attachment model
 */
@Parcelize
data class MessageAttachment(
    val id: String = "",
    val type: MessageType,
    val uri: String,
    val name: String = "",
    val size: Long = 0,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Long? = null,
    val thumbnailUri: String? = null,
    val mimeType: String = ""
) : Parcelable

/**
 * Read receipt model
 */
@Parcelize
data class ReadReceipt(
    val messageId: String,
    val matchId: String,
    val userId: String,
    val readAt: Date = Date()
) : Parcelable

/**
 * Message link preview model
 */
@Parcelize
data class MessageLinkPreview(
    val url: String,
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val siteName: String = ""
) : Parcelable