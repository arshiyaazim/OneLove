package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Match data model representing a match between two users
 */
@Entity(tableName = "matches")
data class Match(
    @PrimaryKey
    val id: String,
    val userId: String,
    val matchedUserId: String,
    val status: MatchStatus = MatchStatus.PENDING,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastMessageText: String? = null,
    val lastMessageTimestamp: Date? = null,
    val unreadCount: Int = 0,
    val isActive: Boolean = true
)

/**
 * Match status enum
 */
enum class MatchStatus {
    PENDING,    // Match requested but not yet accepted
    ACTIVE,     // Match accepted by both parties
    REJECTED,   // Match rejected
    EXPIRED,    // Match expired (no response after certain time)
    BLOCKED     // One party blocked the other
}