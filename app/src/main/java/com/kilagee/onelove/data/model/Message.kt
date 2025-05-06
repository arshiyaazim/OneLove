package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing the type of message
 */
enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, FILE, LOCATION, CONTACT, OFFER, SYSTEM, AI
}

/**
 * Message data class for Firestore mapping
 */
data class Message(
    @DocumentId
    val id: String = "",
    
    // Chat and participant info
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val senderName: String = "",
    val senderPhotoUrl: String? = null,
    
    // Message content
    val content: String = "",
    val messageType: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaThumbnail: String? = null,
    val mediaWidth: Int? = null,
    val mediaHeight: Int? = null,
    val mediaDuration: Long? = null, // For audio/video
    val mediaSize: Long? = null,
    
    // Reference data
    val replyToMessageId: String? = null,
    val referenceData: Map<String, Any>? = null,
    
    // Status flags
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val isSending: Boolean = false,
    val isDeleted: Boolean = false,
    val isEdited: Boolean = false,
    val isAI: Boolean = false,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    
    val readAt: Timestamp? = null,
    val deliveredAt: Timestamp? = null,
    
    // Additional data
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if message has media
     */
    fun hasMedia(): Boolean {
        return messageType in listOf(
            MessageType.IMAGE, MessageType.VIDEO, MessageType.AUDIO, MessageType.FILE
        ) && !mediaUrl.isNullOrEmpty()
    }
    
    /**
     * Get formatted timestamp
     */
    fun getFormattedTime(): String {
        val date = createdAt?.toDate() ?: return ""
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        
        val hours = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(java.util.Calendar.MINUTE)
        
        return String.format("%02d:%02d", hours, minutes)
    }
    
    /**
     * Get shortened content preview
     */
    fun getContentPreview(maxLength: Int = 50): String {
        return when {
            content.isEmpty() -> when (messageType) {
                MessageType.IMAGE -> "📷 Image"
                MessageType.VIDEO -> "🎬 Video"
                MessageType.AUDIO -> "🎵 Audio"
                MessageType.FILE -> "📁 File"
                MessageType.LOCATION -> "📍 Location"
                MessageType.CONTACT -> "👤 Contact"
                MessageType.OFFER -> "🎁 Offer"
                MessageType.SYSTEM -> "System Message"
                MessageType.AI -> "AI Message"
                else -> ""
            }
            content.length <= maxLength -> content
            else -> "${content.take(maxLength)}..."
        }
    }
}