package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kilagee.onelove.data.database.converter.DateConverter
import com.kilagee.onelove.data.database.converter.ListConverter
import java.util.Date

/**
 * Enum for call types
 */
enum class CallType {
    AUDIO,  // Voice-only calls
    VIDEO   // Video calls with audio
}

/**
 * Enum for call status
 */
enum class CallStatus {
    INITIATED,  // Call has been created but not yet established
    ONGOING,    // Call is currently active
    MISSED,     // Call was not answered
    DECLINED,   // Call was declined by receiver
    ENDED,      // Call was ended normally
    FAILED      // Call failed due to technical issues
}

/**
 * Entity class for calls
 */
@Entity(tableName = "calls")
@TypeConverters(DateConverter::class, ListConverter::class)
data class Call(
    @PrimaryKey
    val id: String,
    
    // User who initiated the call
    val callerId: String,
    
    // User receiving the call (empty for group calls)
    val receiverId: String,
    
    // Type of call (audio/video)
    val type: CallType,
    
    // Current status of the call
    val status: CallStatus,
    
    // When the call was answered (null if not answered)
    val startTime: Date?,
    
    // When the call ended (null if not ended)
    val endTime: Date?,
    
    // Duration in seconds (null if not ended)
    val duration: Long?,
    
    // Agora channel name for RTC
    val channelName: String,
    
    // RTC token ID
    val tokenId: String?,
    
    // Creation timestamp
    val createdAt: Date,
    
    // Is this a group call
    val isGroupCall: Boolean = false,
    
    // List of participant IDs for group calls
    val participants: List<String> = emptyList()
)