package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["sender_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["receiver_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Message(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    
    @ColumnInfo(name = "receiver_id")
    val receiverId: String,
    
    @ColumnInfo(name = "content")
    val content: String,
    
    @ColumnInfo(name = "media_url")
    val mediaUrl: String? = null,
    
    @ColumnInfo(name = "media_type")
    val mediaType: MediaType? = null,
    
    @ColumnInfo(name = "sent_at")
    val sentAt: Date = Date(),
    
    @ColumnInfo(name = "read_at")
    val readAt: Date? = null,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    
    @ColumnInfo(name = "is_edited")
    val isEdited: Boolean = false,
    
    @ColumnInfo(name = "message_type")
    val messageType: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    LOCATION,
    OFFER,
    SYSTEM
}

@Entity(
    tableName = "chats",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user1_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user2_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Chat(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "user1_id")
    val user1Id: String,
    
    @ColumnInfo(name = "user2_id")
    val user2Id: String,
    
    @ColumnInfo(name = "last_message_id")
    val lastMessageId: String? = null,
    
    @ColumnInfo(name = "last_active_time")
    val lastActiveTime: Date = Date(),
    
    @ColumnInfo(name = "unread_count")
    val unreadCount: Int = 0,
    
    @ColumnInfo(name = "is_blocked")
    val isBlocked: Boolean = false,
    
    @ColumnInfo(name = "is_muted")
    val isMuted: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date()
)