package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "offers",
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
data class Offer(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    
    @ColumnInfo(name = "receiver_id")
    val receiverId: String,
    
    @ColumnInfo(name = "type")
    val type: OfferType,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "points")
    val points: Int = 0,
    
    @ColumnInfo(name = "amount")
    val amount: Double = 0.0,
    
    @ColumnInfo(name = "status")
    val status: OfferStatus = OfferStatus.PENDING,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date(),
    
    @ColumnInfo(name = "expires_at")
    val expiresAt: Date? = null,
    
    @ColumnInfo(name = "location")
    val location: String? = null,
    
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: Date? = null,
    
    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false
)

enum class OfferType {
    DINNER,
    COFFEE,
    MOVIE,
    WALK,
    CALL,
    VIDEO_CALL,
    CHAT,
    GIFT,
    CUSTOM
}

enum class OfferStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED,
    COMPLETED,
    EXPIRED
}