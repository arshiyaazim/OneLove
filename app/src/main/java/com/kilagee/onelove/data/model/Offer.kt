package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

/**
 * Entity representing an offer between users.
 * An offer can be a date proposal, activity suggestion, etc.
 */
@Entity(tableName = "offers")
data class Offer(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    
    @ColumnInfo(name = "receiver_id")
    val receiverId: String,
    
    @ColumnInfo(name = "type")
    val type: OfferType = OfferType.COFFEE,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String = "",
    
    @ColumnInfo(name = "location")
    val location: String = "",
    
    @ColumnInfo(name = "proposed_time")
    val proposedTime: Date? = null,
    
    @ColumnInfo(name = "status")
    val status: OfferStatus = OfferStatus.PENDING,
    
    @ColumnInfo(name = "points_offered")
    val pointsOffered: Int = 0,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date? = null,
    
    // Additional fields for UI display (not stored in database)
    @ColumnInfo(name = "sender_name")
    val senderName: String? = null,
    
    @ColumnInfo(name = "sender_profile_image_url")
    val senderProfileImageUrl: String? = null,
    
    @ColumnInfo(name = "receiver_name")
    val receiverName: String? = null,
    
    @ColumnInfo(name = "receiver_profile_image_url")
    val receiverProfileImageUrl: String? = null
)

/**
 * Types of offers that can be sent
 */
enum class OfferType {
    COFFEE,
    DINNER,
    MOVIE,
    DRINKS,
    WALK,
    VIDEO_CALL,
    TRAVEL,
    CUSTOM
}

/**
 * Possible statuses for an offer
 */
enum class OfferStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED,
    COMPLETED
}