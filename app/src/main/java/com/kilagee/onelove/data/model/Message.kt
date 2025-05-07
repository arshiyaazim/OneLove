package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

/**
 * Message data model
 * Represents a message between two users or between a user and an AI profile
 */
@Parcelize
data class Message(
    @DocumentId val id: String = "",
    val matchId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null,
    val isRead: Boolean = false,
    val readAt: Timestamp? = null,
    val messageType: String = TYPE_TEXT,
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val mediaThumbnailUrl: String? = null,
    val isFromAI: Boolean = false,
    val reactionType: String? = null,
    val isDeleted: Boolean = false,
    val metadata: Map<String, Any>? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val TYPE_TEXT = "TEXT"
        const val TYPE_IMAGE = "IMAGE"
        const val TYPE_VIDEO = "VIDEO"
        const val TYPE_AUDIO = "AUDIO"
        const val TYPE_LOCATION = "LOCATION"
        const val TYPE_OFFER = "OFFER"
        const val TYPE_SYSTEM = "SYSTEM"
        
        const val REACTION_LIKE = "LIKE"
        const val REACTION_LOVE = "LOVE"
        const val REACTION_LAUGH = "LAUGH"
        const val REACTION_WOW = "WOW"
        const val REACTION_SAD = "SAD"
        const val REACTION_ANGRY = "ANGRY"
    }
}

/**
 * Message Draft data model
 * Represents a draft message that hasn't been sent yet
 */
@Parcelize
data class MessageDraft(
    val matchId: String = "",
    val content: String = "",
    val messageType: String = Message.TYPE_TEXT,
    val mediaUri: String? = null,
    val mediaType: String? = null,
    val timestamp: Timestamp? = null
) : Parcelable