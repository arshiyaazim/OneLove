package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

/**
 * Call data model
 * Represents a voice/video call between users
 */
@Parcelize
data class Call(
    @DocumentId val id: String = "",
    val callerId: String = "",
    val recipientId: String = "",
    val matchId: String = "",
    val callType: String = TYPE_VIDEO,
    val status: String = STATUS_PENDING,
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val duration: Int = 0, // in seconds
    val roomId: String = "",
    val offerSDP: String? = null,
    val answerSDP: String? = null,
    val callerIceCandidates: List<String> = emptyList(),
    val recipientIceCandidates: List<String> = emptyList(),
    val pointsCost: Int = 0,
    val pointsReward: Int = 0,
    val callRating: Int = 0,
    val callFeedback: String? = null,
    val rejectionReason: String? = null,
    val failureReason: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val metadata: Map<String, Any>? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val TYPE_AUDIO = "AUDIO"
        const val TYPE_VIDEO = "VIDEO"
        
        const val STATUS_PENDING = "PENDING"
        const val STATUS_CONNECTING = "CONNECTING"
        const val STATUS_RINGING = "RINGING"
        const val STATUS_CONNECTED = "CONNECTED"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_MISSED = "MISSED"
        const val STATUS_REJECTED = "REJECTED"
        const val STATUS_FAILED = "FAILED"
        const val STATUS_BUSY = "BUSY"
        const val STATUS_CANCELED = "CANCELED"
    }
}

/**
 * CallSchedule data model
 * Represents a scheduled future call between users
 */
@Parcelize
data class CallSchedule(
    @DocumentId val id: String = "",
    val userId1: String = "",
    val userId2: String = "",
    val matchId: String = "",
    val callType: String = Call.TYPE_VIDEO,
    val scheduledTime: Timestamp? = null,
    val reminderSent: Boolean = false,
    val reminderSentAt: Timestamp? = null,
    val status: String = STATUS_SCHEDULED,
    val callId: String? = null, // Linked to actual call when it happens
    val notes: String? = null,
    val pointsCost: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val STATUS_SCHEDULED = "SCHEDULED"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_CANCELED = "CANCELED"
        const val STATUS_MISSED = "MISSED"
    }
}