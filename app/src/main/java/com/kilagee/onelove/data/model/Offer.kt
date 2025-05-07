package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize

/**
 * Offer data model
 * Represents an offer that one user can make to another, such as a date, call, etc.
 */
@Parcelize
data class Offer(
    @DocumentId val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val matchId: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = TYPE_DATE,
    val dateTime: Timestamp? = null,
    val endDateTime: Timestamp? = null,
    val location: GeoPoint? = null,
    val locationName: String = "",
    val locationAddress: String = "",
    val pointsCost: Int = 0,
    val pointsReward: Int = 0,
    val imageUrl: String? = null,
    val activities: List<String> = emptyList(),
    val status: String = STATUS_PENDING,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val respondedAt: Timestamp? = null,
    val isDeleted: Boolean = false,
    val metadata: Map<String, Any>? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val TYPE_DATE = "DATE"
        const val TYPE_CALL = "CALL"
        const val TYPE_MEETING = "MEETING"
        const val TYPE_VIRTUAL = "VIRTUAL"
        const val TYPE_GIFT = "GIFT"
        const val TYPE_CUSTOM = "CUSTOM"
        
        const val STATUS_PENDING = "PENDING"
        const val STATUS_ACCEPTED = "ACCEPTED"
        const val STATUS_DECLINED = "DECLINED"
        const val STATUS_EXPIRED = "EXPIRED"
        const val STATUS_CANCELED = "CANCELED"
        const val STATUS_COMPLETED = "COMPLETED"
    }
}

/**
 * UserOffer data model
 * Represents a reference to an offer with user-specific data
 */
@Parcelize
data class UserOffer(
    @DocumentId val id: String = "",
    val userId: String = "",
    val offerId: String = "",
    val isFromUser: Boolean = false,
    val isRead: Boolean = false,
    val isNudged: Boolean = false,
    val lastNudgedAt: Timestamp? = null,
    val nudgeCount: Int = 0
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
}

/**
 * OfferActivity data model
 * Represents an activity that can be selected for an offer
 */
@Parcelize
data class OfferActivity(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = "",
    val icon: String = "",
    val sortOrder: Int = 0,
    val isActive: Boolean = true
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
}