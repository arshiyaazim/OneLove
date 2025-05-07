package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

/**
 * Match data model
 * Represents a match between two users
 */
@Parcelize
data class Match(
    @DocumentId val id: String = "",
    val userId1: String = "",
    val userId2: String = "",
    val matchedAt: Timestamp? = null,
    val lastInteractionAt: Timestamp? = null,
    val status: String = STATUS_ACTIVE,
    val unreadUser1: Int = 0,
    val unreadUser2: Int = 0,
    val user1SuperLiked: Boolean = false,
    val user2SuperLiked: Boolean = false
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_PAUSED = "PAUSED"
        const val STATUS_UNMATCHED = "UNMATCHED"
        const val STATUS_BLOCKED = "BLOCKED"
    }
}

/**
 * Like data model
 * Represents a like from one user to another
 */
@Parcelize
data class Like(
    @DocumentId val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val createdAt: Timestamp? = null,
    val isSuperLike: Boolean = false,
    val seen: Boolean = false
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
}

/**
 * Skip data model
 * Represents a user skipping another user's profile
 */
@Parcelize
data class Skip(
    @DocumentId val id: String = "",
    val userId: String = "",
    val skippedUserId: String = "",
    val timestamp: Timestamp? = null,
    val reason: String? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
}