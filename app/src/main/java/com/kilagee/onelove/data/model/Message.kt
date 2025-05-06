package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Sealed class representing different message types with their specific properties
 */
sealed class MessageContent {
    data class Text(
        val text: String = ""
    ) : MessageContent()
    
    data class Image(
        val imageUrl: String = "",
        val thumbnailUrl: String? = null,
        val width: Int = 0,
        val height: Int = 0,
        val caption: String? = null,
        val size: Long = 0
    ) : MessageContent()
    
    data class Video(
        val videoUrl: String = "",
        val thumbnailUrl: String? = null,
        val width: Int = 0,
        val height: Int = 0,
        val duration: Long = 0,
        val caption: String? = null,
        val size: Long = 0
    ) : MessageContent()
    
    data class Audio(
        val audioUrl: String = "",
        val duration: Long = 0,
        val caption: String? = null,
        val size: Long = 0,
        val waveformData: List<Float>? = null
    ) : MessageContent()
    
    data class File(
        val fileUrl: String = "",
        val fileName: String = "",
        val fileType: String = "",
        val size: Long = 0
    ) : MessageContent()
    
    data class Location(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val address: String? = null,
        val name: String? = null
    ) : MessageContent()
    
    data class Contact(
        val userId: String? = null,
        val name: String = "",
        val phoneNumber: String? = null,
        val email: String? = null,
        val photoUrl: String? = null
    ) : MessageContent()
    
    data class Offer(
        val offerId: String = "",
        val offerType: String = "",
        val title: String = "",
        val description: String? = null,
        val amount: Double? = null,
        val currency: String? = null,
        val expiryTime: Timestamp? = null,
        val mediaUrl: String? = null,
        val thumbnailUrl: String? = null
    ) : MessageContent()
    
    data class System(
        val systemMessageType: SystemMessageType = SystemMessageType.INFO,
        val text: String = ""
    ) : MessageContent()
    
    data class AI(
        val aiProfileId: String = "",
        val text: String = "",
        val sentiment: String? = null,
        val mediaUrl: String? = null,
        val responseMetadata: Map<String, Any> = emptyMap()
    ) : MessageContent()
}

/**
 * Enum representing system message types
 */
enum class SystemMessageType {
    INFO, WARNING, ERROR, MATCH, UNMATCH, SUBSCRIPTION, OFFER_ACCEPTED, OFFER_DECLINED
}

/**
 * Enum representing message delivery status
 */
enum class MessageDeliveryStatus {
    SENDING, SENT, DELIVERED, READ, FAILED
}

/**
 * Enum representing the type of message
 */
enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, FILE, LOCATION, CONTACT, OFFER, SYSTEM, AI;
    
    companion object {
        fun fromMessageContent(content: MessageContent): MessageType {
            return when (content) {
                is MessageContent.Text -> TEXT
                is MessageContent.Image -> IMAGE
                is MessageContent.Video -> VIDEO
                is MessageContent.Audio -> AUDIO
                is MessageContent.File -> FILE
                is MessageContent.Location -> LOCATION
                is MessageContent.Contact -> CONTACT
                is MessageContent.Offer -> OFFER
                is MessageContent.System -> SYSTEM
                is MessageContent.AI -> AI
            }
        }
    }
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
    val recipientName: String = "",
    val recipientPhotoUrl: String? = null,
    
    // Message content (for backward compatibility)
    val content: String = "",
    val messageType: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaThumbnail: String? = null,
    val mediaWidth: Int? = null,
    val mediaHeight: Int? = null,
    val mediaDuration: Long? = null, // For audio/video
    val mediaSize: Long? = null,
    
    // Content Map - serialized MessageContent for type safety
    @get:PropertyName("contentMap")
    @set:PropertyName("contentMap")
    var contentMap: Map<String, Any> = emptyMap(),
    
    // Chat features
    val translation: Map<String, String> = emptyMap(), // Map of language code to translated content
    val language: String? = null, // Source language of the message
    
    // Reference data
    val replyToMessageId: String? = null,
    val replyToContent: String? = null,
    val replyToSenderName: String? = null,
    val forwardedFrom: String? = null,
    val referenceData: Map<String, Any> = emptyMap(),
    
    // Status flags
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val isSending: Boolean = false,
    val isDeleted: Boolean = false,
    val isDeletedForSender: Boolean = false,
    val isDeletedForReceiver: Boolean = false,
    val isEdited: Boolean = false,
    val isForwarded: Boolean = false,
    val isPinned: Boolean = false,
    val isAI: Boolean = false,
    val isMuted: Boolean = false,
    val deliveryStatus: MessageDeliveryStatus = MessageDeliveryStatus.SENDING,
    val errorMessage: String? = null,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    
    val readAt: Timestamp? = null,
    val deliveredAt: Timestamp? = null,
    val editedAt: Timestamp? = null,
    val scheduledFor: Timestamp? = null,
    val expiresAt: Timestamp? = null,
    
    // Additional data
    val reactions: Map<String, String> = emptyMap(), // userId to emoji reaction
    val reactionCount: Map<String, Int> = emptyMap(), // Emoji to count
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Get the typed message content
     */
    @Exclude
    fun getTypedContent(): MessageContent {
        // Convert from contentMap if available
        if (contentMap.isNotEmpty()) {
            return when (messageType) {
                MessageType.TEXT -> MessageContent.Text(
                    contentMap["text"] as? String ?: content
                )
                MessageType.IMAGE -> MessageContent.Image(
                    imageUrl = contentMap["imageUrl"] as? String ?: mediaUrl ?: "",
                    thumbnailUrl = contentMap["thumbnailUrl"] as? String ?: mediaThumbnail,
                    width = (contentMap["width"] as? Number)?.toInt() ?: mediaWidth ?: 0,
                    height = (contentMap["height"] as? Number)?.toInt() ?: mediaHeight ?: 0,
                    caption = contentMap["caption"] as? String,
                    size = (contentMap["size"] as? Number)?.toLong() ?: mediaSize ?: 0
                )
                MessageType.VIDEO -> MessageContent.Video(
                    videoUrl = contentMap["videoUrl"] as? String ?: mediaUrl ?: "",
                    thumbnailUrl = contentMap["thumbnailUrl"] as? String ?: mediaThumbnail,
                    width = (contentMap["width"] as? Number)?.toInt() ?: mediaWidth ?: 0,
                    height = (contentMap["height"] as? Number)?.toInt() ?: mediaHeight ?: 0,
                    duration = (contentMap["duration"] as? Number)?.toLong() ?: mediaDuration ?: 0,
                    caption = contentMap["caption"] as? String,
                    size = (contentMap["size"] as? Number)?.toLong() ?: mediaSize ?: 0
                )
                MessageType.AUDIO -> MessageContent.Audio(
                    audioUrl = contentMap["audioUrl"] as? String ?: mediaUrl ?: "",
                    duration = (contentMap["duration"] as? Number)?.toLong() ?: mediaDuration ?: 0,
                    caption = contentMap["caption"] as? String,
                    size = (contentMap["size"] as? Number)?.toLong() ?: mediaSize ?: 0,
                    waveformData = contentMap["waveformData"] as? List<Float>
                )
                MessageType.FILE -> MessageContent.File(
                    fileUrl = contentMap["fileUrl"] as? String ?: mediaUrl ?: "",
                    fileName = contentMap["fileName"] as? String ?: "",
                    fileType = contentMap["fileType"] as? String ?: "",
                    size = (contentMap["size"] as? Number)?.toLong() ?: mediaSize ?: 0
                )
                MessageType.LOCATION -> {
                    val lat = (contentMap["latitude"] as? Number)?.toDouble() ?: 0.0
                    val lng = (contentMap["longitude"] as? Number)?.toDouble() ?: 0.0
                    MessageContent.Location(
                        latitude = lat,
                        longitude = lng,
                        address = contentMap["address"] as? String,
                        name = contentMap["name"] as? String
                    )
                }
                MessageType.CONTACT -> MessageContent.Contact(
                    userId = contentMap["userId"] as? String,
                    name = contentMap["name"] as? String ?: "",
                    phoneNumber = contentMap["phoneNumber"] as? String,
                    email = contentMap["email"] as? String,
                    photoUrl = contentMap["photoUrl"] as? String
                )
                MessageType.OFFER -> MessageContent.Offer(
                    offerId = contentMap["offerId"] as? String ?: "",
                    offerType = contentMap["offerType"] as? String ?: "",
                    title = contentMap["title"] as? String ?: "",
                    description = contentMap["description"] as? String,
                    amount = (contentMap["amount"] as? Number)?.toDouble(),
                    currency = contentMap["currency"] as? String,
                    expiryTime = contentMap["expiryTime"] as? Timestamp,
                    mediaUrl = contentMap["mediaUrl"] as? String,
                    thumbnailUrl = contentMap["thumbnailUrl"] as? String
                )
                MessageType.SYSTEM -> {
                    val typeStr = contentMap["systemMessageType"] as? String ?: SystemMessageType.INFO.name
                    val type = try {
                        SystemMessageType.valueOf(typeStr)
                    } catch (e: Exception) {
                        SystemMessageType.INFO
                    }
                    MessageContent.System(
                        systemMessageType = type,
                        text = contentMap["text"] as? String ?: content
                    )
                }
                MessageType.AI -> MessageContent.AI(
                    aiProfileId = contentMap["aiProfileId"] as? String ?: "",
                    text = contentMap["text"] as? String ?: content,
                    sentiment = contentMap["sentiment"] as? String,
                    mediaUrl = contentMap["mediaUrl"] as? String,
                    responseMetadata = contentMap["responseMetadata"] as? Map<String, Any> ?: emptyMap()
                )
            }
        }
        
        // Fallback to legacy fields
        return when (messageType) {
            MessageType.TEXT -> MessageContent.Text(content)
            MessageType.IMAGE -> MessageContent.Image(
                imageUrl = mediaUrl ?: "",
                thumbnailUrl = mediaThumbnail,
                width = mediaWidth ?: 0,
                height = mediaHeight ?: 0,
                size = mediaSize ?: 0
            )
            MessageType.VIDEO -> MessageContent.Video(
                videoUrl = mediaUrl ?: "",
                thumbnailUrl = mediaThumbnail,
                width = mediaWidth ?: 0,
                height = mediaHeight ?: 0,
                duration = mediaDuration ?: 0,
                size = mediaSize ?: 0
            )
            MessageType.AUDIO -> MessageContent.Audio(
                audioUrl = mediaUrl ?: "",
                duration = mediaDuration ?: 0,
                size = mediaSize ?: 0
            )
            MessageType.FILE -> MessageContent.File(
                fileUrl = mediaUrl ?: "",
                fileName = content,
                fileType = "",
                size = mediaSize ?: 0
            )
            MessageType.LOCATION -> {
                // Parse location from content (expected format: "lat,lng")
                val parts = content.split(",")
                val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                val lng = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                MessageContent.Location(
                    latitude = lat,
                    longitude = lng
                )
            }
            MessageType.CONTACT -> {
                // Parse name from content
                MessageContent.Contact(
                    name = content
                )
            }
            MessageType.OFFER -> {
                // Basic offer with title from content
                MessageContent.Offer(
                    title = content,
                    mediaUrl = mediaUrl
                )
            }
            MessageType.SYSTEM -> MessageContent.System(
                text = content
            )
            MessageType.AI -> MessageContent.AI(
                text = content
            )
        }
    }
    
    /**
     * Create the contentMap from typed content
     */
    @Exclude
    fun createContentMap(content: MessageContent): Map<String, Any> {
        return when (content) {
            is MessageContent.Text -> mapOf(
                "text" to content.text
            )
            is MessageContent.Image -> mapOf(
                "imageUrl" to content.imageUrl,
                "thumbnailUrl" to (content.thumbnailUrl ?: ""),
                "width" to content.width,
                "height" to content.height,
                "caption" to (content.caption ?: ""),
                "size" to content.size
            ).filterValues { it != "" }
            is MessageContent.Video -> mapOf(
                "videoUrl" to content.videoUrl,
                "thumbnailUrl" to (content.thumbnailUrl ?: ""),
                "width" to content.width,
                "height" to content.height,
                "duration" to content.duration,
                "caption" to (content.caption ?: ""),
                "size" to content.size
            ).filterValues { it != "" }
            is MessageContent.Audio -> mapOf(
                "audioUrl" to content.audioUrl,
                "duration" to content.duration,
                "caption" to (content.caption ?: ""),
                "size" to content.size,
                "waveformData" to (content.waveformData ?: emptyList<Float>())
            ).filterValues { it != "" && it != emptyList<Float>() }
            is MessageContent.File -> mapOf(
                "fileUrl" to content.fileUrl,
                "fileName" to content.fileName,
                "fileType" to content.fileType,
                "size" to content.size
            )
            is MessageContent.Location -> mapOf(
                "latitude" to content.latitude,
                "longitude" to content.longitude,
                "address" to (content.address ?: ""),
                "name" to (content.name ?: "")
            ).filterValues { it != "" }
            is MessageContent.Contact -> mapOf(
                "userId" to (content.userId ?: ""),
                "name" to content.name,
                "phoneNumber" to (content.phoneNumber ?: ""),
                "email" to (content.email ?: ""),
                "photoUrl" to (content.photoUrl ?: "")
            ).filterValues { it != "" }
            is MessageContent.Offer -> mapOf(
                "offerId" to content.offerId,
                "offerType" to content.offerType,
                "title" to content.title,
                "description" to (content.description ?: ""),
                "amount" to (content.amount ?: 0.0),
                "currency" to (content.currency ?: ""),
                "expiryTime" to (content.expiryTime ?: Timestamp.now()),
                "mediaUrl" to (content.mediaUrl ?: ""),
                "thumbnailUrl" to (content.thumbnailUrl ?: "")
            ).filterValues { it != "" && it != 0.0 }
            is MessageContent.System -> mapOf(
                "systemMessageType" to content.systemMessageType.name,
                "text" to content.text
            )
            is MessageContent.AI -> mapOf(
                "aiProfileId" to content.aiProfileId,
                "text" to content.text,
                "sentiment" to (content.sentiment ?: ""),
                "mediaUrl" to (content.mediaUrl ?: ""),
                "responseMetadata" to content.responseMetadata
            ).filterValues { it != "" && it != emptyMap<String, Any>() }
        }
    }
    
    /**
     * Check if message has media
     */
    @Exclude
    fun hasMedia(): Boolean {
        return when (messageType) {
            MessageType.IMAGE, MessageType.VIDEO, MessageType.AUDIO, MessageType.FILE -> true
            MessageType.OFFER -> {
                val typedContent = getTypedContent()
                if (typedContent is MessageContent.Offer) {
                    !typedContent.mediaUrl.isNullOrEmpty()
                } else {
                    mediaUrl != null
                }
            }
            MessageType.AI -> {
                val typedContent = getTypedContent()
                if (typedContent is MessageContent.AI) {
                    !typedContent.mediaUrl.isNullOrEmpty()
                } else {
                    mediaUrl != null
                }
            }
            else -> false
        }
    }
    
    /**
     * Get formatted timestamp
     */
    @Exclude
    fun getFormattedTime(pattern: String = "HH:mm", locale: Locale = Locale.getDefault()): String {
        val date = createdAt?.toDate() ?: return ""
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(date)
    }
    
    /**
     * Get relative time (e.g., "2h ago")
     */
    @Exclude
    fun getRelativeTime(): String {
        val date = createdAt?.toDate() ?: return ""
        val now = Date()
        val diff = now.time - date.time
        
        return when {
            diff < 60 * 1000 -> "just now"
            diff < 60 * 60 * 1000 -> "${(diff / (60 * 1000)).toInt()}m ago"
            diff < 24 * 60 * 60 * 1000 -> "${(diff / (60 * 60 * 1000)).toInt()}h ago"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${(diff / (24 * 60 * 60 * 1000)).toInt()}d ago"
            else -> {
                val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
                formatter.format(date)
            }
        }
    }
    
    /**
     * Get shortened content preview
     */
    @Exclude
    fun getContentPreview(maxLength: Int = 50): String {
        val typedContent = getTypedContent()
        
        val previewText = when (typedContent) {
            is MessageContent.Text -> typedContent.text
            is MessageContent.Image -> typedContent.caption ?: "📷 Image"
            is MessageContent.Video -> typedContent.caption ?: "🎬 Video"
            is MessageContent.Audio -> typedContent.caption ?: "🎵 Audio"
            is MessageContent.File -> "📁 ${typedContent.fileName}"
            is MessageContent.Location -> typedContent.name ?: typedContent.address ?: "📍 Location"
            is MessageContent.Contact -> "👤 ${typedContent.name}"
            is MessageContent.Offer -> "🎁 ${typedContent.title}"
            is MessageContent.System -> typedContent.text
            is MessageContent.AI -> typedContent.text
        }
        
        return when {
            previewText.isEmpty() -> when (messageType) {
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
            previewText.length <= maxLength -> previewText
            else -> "${previewText.take(maxLength)}..."
        }
    }
    
    /**
     * Get translation for a specific language
     */
    @Exclude
    fun getTranslation(languageCode: String): String? {
        return translation[languageCode]
    }
    
    /**
     * Companion object to create message from typed content
     */
    companion object {
        fun createFromContent(
            id: String = "",
            chatId: String,
            senderId: String,
            receiverId: String,
            senderName: String,
            content: MessageContent,
            replyToMessageId: String? = null
        ): Message {
            // Fill in basic fields
            val message = Message(
                id = id,
                chatId = chatId,
                senderId = senderId,
                receiverId = receiverId,
                senderName = senderName,
                messageType = MessageType.fromMessageContent(content),
                replyToMessageId = replyToMessageId,
                isAI = content is MessageContent.AI
            )
            
            // Create content map for new format
            val contentMap = message.createContentMap(content)
            
            // Also update legacy fields for backward compatibility
            val (legacyContent, legacyMediaUrl) = when (content) {
                is MessageContent.Text -> Pair(content.text, null)
                is MessageContent.Image -> Pair(content.caption ?: "", content.imageUrl)
                is MessageContent.Video -> Pair(content.caption ?: "", content.videoUrl)
                is MessageContent.Audio -> Pair(content.caption ?: "", content.audioUrl)
                is MessageContent.File -> Pair(content.fileName, content.fileUrl)
                is MessageContent.Location -> {
                    val locationString = "${content.latitude},${content.longitude}"
                    Pair(locationString, null)
                }
                is MessageContent.Contact -> Pair(content.name, null)
                is MessageContent.Offer -> Pair(content.title, content.mediaUrl)
                is MessageContent.System -> Pair(content.text, null)
                is MessageContent.AI -> Pair(content.text, content.mediaUrl)
            }
            
            return message.copy(
                content = legacyContent,
                mediaUrl = legacyMediaUrl,
                mediaThumbnail = when (content) {
                    is MessageContent.Image -> content.thumbnailUrl
                    is MessageContent.Video -> content.thumbnailUrl
                    else -> null
                },
                mediaWidth = when (content) {
                    is MessageContent.Image -> content.width
                    is MessageContent.Video -> content.width
                    else -> null
                },
                mediaHeight = when (content) {
                    is MessageContent.Image -> content.height
                    is MessageContent.Video -> content.height
                    else -> null
                },
                mediaDuration = when (content) {
                    is MessageContent.Video -> content.duration
                    is MessageContent.Audio -> content.duration
                    else -> null
                },
                mediaSize = when (content) {
                    is MessageContent.Image -> content.size
                    is MessageContent.Video -> content.size
                    is MessageContent.Audio -> content.size
                    is MessageContent.File -> content.size
                    else -> null
                },
                contentMap = contentMap
            )
        }
    }
}