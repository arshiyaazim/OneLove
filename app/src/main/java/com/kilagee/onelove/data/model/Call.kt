package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing call type
 */
enum class CallType {
    AUDIO, VIDEO
}

/**
 * Enum representing call status
 */
enum class CallStatus {
    INITIATED, RINGING, ONGOING, ENDED, MISSED, REJECTED, FAILED
}

/**
 * Call data class for Firestore mapping
 */
data class Call(
    @DocumentId
    val id: String = "",
    
    // Participants
    val callerId: String = "",
    val receiverId: String = "",
    val callerName: String = "",
    val receiverName: String = "",
    val callerPhotoUrl: String? = null,
    val receiverPhotoUrl: String? = null,
    
    // Call details
    val callType: CallType = CallType.AUDIO,
    val status: CallStatus = CallStatus.INITIATED,
    val duration: Long = 0, // in seconds
    val quality: Int = 0, // 0-5 rating
    
    // Technical details
    val channelId: String = "",
    val token: String? = null,
    val signalProvider: String = "agora", // or "twilio", etc.
    val serverRegion: String? = null,
    val connectConfig: Map<String, Any> = emptyMap(),
    
    // Timestamps
    @ServerTimestamp
    val startedAt: Timestamp? = null,
    
    val endedAt: Timestamp? = null,
    val answeredAt: Timestamp? = null,
    
    // Recording
    val isRecorded: Boolean = false,
    val recordingUrl: String? = null,
    
    // Related entities
    val matchId: String? = null,
    val chatId: String? = null,
    val offerId: String? = null,
    
    // Costs
    val pointsCost: Int = 0,
    val pointsCharged: Boolean = false,
    
    // Additional data
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if call is active
     */
    fun isActive(): Boolean {
        return status == CallStatus.INITIATED || 
               status == CallStatus.RINGING || 
               status == CallStatus.ONGOING
    }
    
    /**
     * Check if call was completed successfully
     */
    fun isCompleted(): Boolean {
        return status == CallStatus.ENDED && duration > 0
    }
    
    /**
     * Check if call was missed
     */
    fun isMissed(): Boolean {
        return status == CallStatus.MISSED || 
               (status == CallStatus.ENDED && duration == 0L)
    }
    
    /**
     * Get formatted call type
     */
    fun getFormattedCallType(): String {
        return when (callType) {
            CallType.AUDIO -> "Audio Call"
            CallType.VIDEO -> "Video Call"
        }
    }
    
    /**
     * Get formatted call status
     */
    fun getFormattedStatus(): String {
        return when (status) {
            CallStatus.INITIATED -> "Calling..."
            CallStatus.RINGING -> "Ringing..."
            CallStatus.ONGOING -> "Ongoing"
            CallStatus.ENDED -> "Ended"
            CallStatus.MISSED -> "Missed"
            CallStatus.REJECTED -> "Rejected"
            CallStatus.FAILED -> "Failed"
        }
    }
    
    /**
     * Get formatted duration
     */
    fun getFormattedDuration(): String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    /**
     * Get formatted timestamp
     */
    fun getFormattedTime(): String {
        val date = startedAt?.toDate() ?: return ""
        val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Get formatted date
     */
    fun getFormattedDate(): String {
        val date = startedAt?.toDate() ?: return ""
        val formatter = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }
}