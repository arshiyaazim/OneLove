package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Call model representing a call between users
 */
@Parcelize
data class Call(
    val id: String = "",
    val matchId: String = "",
    val callerId: String = "",
    val receiverId: String = "",
    val type: CallType = CallType.AUDIO,
    val status: CallStatus = CallStatus.RINGING,
    val startTime: Date? = null,
    val endTime: Date? = null,
    val duration: Long = 0, // Duration in seconds
    val endReason: CallEndReason = CallEndReason.NORMAL,
    val quality: Int = 0, // 1-5 rating
    val metadata: Map<String, String> = emptyMap(),
    val screenshotUrl: String = "", // For video calls
    val isRecorded: Boolean = false,
    val recordingUrl: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

/**
 * Call type enum
 */
@Parcelize
enum class CallType : Parcelable {
    AUDIO,
    VIDEO
}

/**
 * Call status enum
 */
@Parcelize
enum class CallStatus : Parcelable {
    RINGING,
    CONNECTING,
    CONNECTED,
    DECLINED,
    MISSED,
    ENDED,
    FAILED
}

/**
 * Call end reason enum
 */
@Parcelize
enum class CallEndReason : Parcelable {
    NORMAL, // Normal end
    DECLINED, // Receiver declined
    MISSED, // Receiver didn't answer
    TIMEOUT, // Call timed out
    CONNECTION_ERROR, // Connection error
    CALLER_ENDED, // Caller ended
    RECEIVER_ENDED, // Receiver ended
    SYSTEM_ENDED // System ended (e.g., low battery, incoming phone call)
}

/**
 * Call preference model
 */
@Parcelize
data class CallPreference(
    val userId: String,
    val preferVideo: Boolean = true,
    val enableBackgroundBlur: Boolean = false,
    val enableNoiseReduction: Boolean = true,
    val allowRecording: Boolean = false,
    val defaultMicrophoneMuted: Boolean = false,
    val defaultCameraMuted: Boolean = false,
    val preferSpeaker: Boolean = true
) : Parcelable

/**
 * Call signal event for WebRTC signaling
 */
@Parcelize
data class CallSignalEvent(
    val callId: String,
    val senderId: String,
    val recipientId: String,
    val type: CallSignalType,
    val data: Map<String, Any> = emptyMap(),
    val createdAt: Date = Date()
) : Parcelable {
    @Suppress("UNCHECKED_CAST")
    fun getStringData(): Map<String, String> {
        return data.mapValues { it.value.toString() }
    }
}

/**
 * Call signal type enum
 */
@Parcelize
enum class CallSignalType : Parcelable {
    OFFER, // WebRTC offer
    ANSWER, // WebRTC answer
    ICE_CANDIDATE, // WebRTC ICE candidate
    MUTE_AUDIO, // Audio muted
    UNMUTE_AUDIO, // Audio unmuted
    MUTE_VIDEO, // Video muted
    UNMUTE_VIDEO, // Video unmuted
    SWITCH_CAMERA, // Switch camera
    ENABLE_SCREEN_SHARE, // Enable screen sharing
    DISABLE_SCREEN_SHARE, // Disable screen sharing
    CONNECTION_STATE, // Connection state change
    BANDWIDTH_CHANGE, // Bandwidth change
    ERROR // Error
}