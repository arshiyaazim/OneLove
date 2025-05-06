package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Chat data class for Firestore mapping
 */
data class Chat(
    @DocumentId
    val id: String = "",
    
    // Basic info
    val name: String = "",
    val description: String = "",
    val photoUrl: String? = null,
    
    // Participants
    val participantIds: List<String> = emptyList(),
    val participantNames: List<String> = emptyList(),
    val participantPhotos: List<String> = emptyList(),
    val isGroupChat: Boolean = false,
    val adminIds: List<String> = emptyList(),
    
    // Last message
    val lastMessage: String = "",
    val lastMessageType: String = "",
    val lastMessageTime: Timestamp? = null,
    val lastMessageSenderId: String = "",
    
    // Status
    val isActive: Boolean = true,
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val unreadCounts: Map<String, Int> = emptyMap(),
    val typingUsers: List<String> = emptyList(),
    
    // Settings
    val muteNotifications: Map<String, Boolean> = emptyMap(),
    val customNotificationSound: Map<String, String> = emptyMap(),
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    
    // Metadata
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Get chat name for a given user (for 1:1 chats)
     */
    fun getChatNameForUser(userId: String): String {
        return if (isGroupChat || participantIds.size != 2) {
            name
        } else {
            val index = participantIds.indexOf(userId)
            if (index == 0 && participantNames.size > 1) {
                participantNames[1]
            } else if (index == 1 && participantNames.isNotEmpty()) {
                participantNames[0]
            } else {
                name
            }
        }
    }
    
    /**
     * Get chat avatar URL for a given user (for 1:1 chats)
     */
    fun getChatAvatarForUser(userId: String): String? {
        return if (isGroupChat || participantIds.size != 2) {
            photoUrl
        } else {
            val index = participantIds.indexOf(userId)
            if (index == 0 && participantPhotos.size > 1) {
                participantPhotos[1]
            } else if (index == 1 && participantPhotos.isNotEmpty()) {
                participantPhotos[0]
            } else {
                photoUrl
            }
        }
    }
    
    /**
     * Get the other participant's ID in a 1:1 chat
     */
    fun getOtherParticipantId(userId: String): String? {
        return if (isGroupChat || participantIds.size != 2) {
            null
        } else {
            participantIds.firstOrNull { it != userId }
        }
    }
    
    /**
     * Get unread count for a user
     */
    fun getUnreadCountForUser(userId: String): Int {
        return unreadCounts[userId] ?: 0
    }
    
    /**
     * Check if notifications are muted for a user
     */
    fun isNotificationsMutedForUser(userId: String): Boolean {
        return muteNotifications[userId] == true
    }
    
    /**
     * Get formatted last message time
     */
    fun getFormattedLastMessageTime(): String {
        val date = lastMessageTime?.toDate() ?: return ""
        val now = java.util.Date()
        val diff = now.time - date.time
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes min"
            hours < 24 -> "$hours h"
            days < 7 -> "$days d"
            else -> {
                val formatter = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                formatter.format(date)
            }
        }
    }
}