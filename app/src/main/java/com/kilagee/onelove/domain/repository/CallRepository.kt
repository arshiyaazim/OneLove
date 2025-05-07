package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Call
import com.kilagee.onelove.data.model.CallOffer
import com.kilagee.onelove.data.model.CallSession
import com.kilagee.onelove.data.model.CallSettings
import com.kilagee.onelove.data.model.CallType
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Interface for call-related operations
 */
interface CallRepository {
    
    /**
     * Get call history for the current user
     * @param limit Maximum number of calls to fetch
     * @param before Optional timestamp to fetch calls before (for pagination)
     * @return List of [Call] objects
     */
    suspend fun getCallHistory(limit: Int = 20, before: Date? = null): Result<List<Call>>
    
    /**
     * Get call history as a flow for real-time updates
     * @param limit Maximum number of calls to fetch initially
     * @return Flow of [Call] lists
     */
    fun getCallHistoryFlow(limit: Int = 20): Flow<Result<List<Call>>>
    
    /**
     * Get a specific call by ID
     * @param callId ID of the call to retrieve
     * @return The [Call] object
     */
    suspend fun getCall(callId: String): Result<Call>
    
    /**
     * Get call as a flow for real-time updates
     * @param callId ID of the call to retrieve
     * @return Flow of the [Call]
     */
    fun getCallFlow(callId: String): Flow<Result<Call>>
    
    /**
     * Initialize a call to a user
     * @param userId ID of the user to call
     * @param type Type of call (audio or video)
     * @param settings Optional call settings
     * @return The created [Call] object
     */
    suspend fun initiateCall(
        userId: String,
        type: CallType,
        settings: CallSettings? = null
    ): Result<Call>
    
    /**
     * Accept an incoming call
     * @param callId ID of the call to accept
     * @param enableVideo Whether to enable video
     * @return The updated [Call] object
     */
    suspend fun acceptCall(callId: String, enableVideo: Boolean = true): Result<Call>
    
    /**
     * Reject an incoming call
     * @param callId ID of the call to reject
     * @param reason Optional reason for rejection
     */
    suspend fun rejectCall(callId: String, reason: String? = null): Result<Unit>
    
    /**
     * End an ongoing call
     * @param callId ID of the call to end
     * @param reason Optional reason for ending
     */
    suspend fun endCall(callId: String, reason: String? = null): Result<Unit>
    
    /**
     * Toggle mute status during a call
     * @param callId ID of the call
     * @param muted Whether audio should be muted
     */
    suspend fun toggleMute(callId: String, muted: Boolean): Result<Unit>
    
    /**
     * Toggle video status during a call
     * @param callId ID of the call
     * @param videoEnabled Whether video should be enabled
     */
    suspend fun toggleVideo(callId: String, videoEnabled: Boolean): Result<Unit>
    
    /**
     * Toggle speaker status during a call
     * @param callId ID of the call
     * @param speakerOn Whether speaker should be on
     */
    suspend fun toggleSpeaker(callId: String, speakerOn: Boolean): Result<Unit>
    
    /**
     * Switch camera during a video call
     * @param callId ID of the call
     * @param useFrontCamera Whether to use the front camera
     */
    suspend fun switchCamera(callId: String, useFrontCamera: Boolean): Result<Unit>
    
    /**
     * Create a call offer (WebRTC)
     * @param callId ID of the call
     * @param receiverId ID of the user receiving the offer
     * @param sdp Session Description Protocol string
     * @return The created [CallOffer]
     */
    suspend fun createCallOffer(
        callId: String,
        receiverId: String,
        sdp: String
    ): Result<CallOffer>
    
    /**
     * Answer a call offer (WebRTC)
     * @param offerId ID of the offer to answer
     * @param sdp Session Description Protocol string
     */
    suspend fun answerCallOffer(offerId: String, sdp: String): Result<Unit>
    
    /**
     * Add ICE candidate (WebRTC)
     * @param callId ID of the call
     * @param candidate ICE candidate JSON string
     */
    suspend fun addIceCandidate(callId: String, candidate: String): Result<Unit>
    
    /**
     * Get active call session
     * @param callId ID of the call
     * @return The [CallSession] object
     */
    suspend fun getCallSession(callId: String): Result<CallSession>
    
    /**
     * Get call session as a flow for real-time updates
     * @param callId ID of the call
     * @return Flow of the [CallSession]
     */
    fun getCallSessionFlow(callId: String): Flow<Result<CallSession>>
    
    /**
     * Start screen sharing during a call
     * @param callId ID of the call
     * @return Updated [CallSession]
     */
    suspend fun startScreenSharing(callId: String): Result<CallSession>
    
    /**
     * Stop screen sharing during a call
     * @param callId ID of the call
     */
    suspend fun stopScreenSharing(callId: String): Result<Unit>
    
    /**
     * Start call recording
     * @param callId ID of the call
     * @return Updated [CallSession]
     */
    suspend fun startRecording(callId: String): Result<CallSession>
    
    /**
     * Stop call recording
     * @param callId ID of the call
     */
    suspend fun stopRecording(callId: String): Result<Unit>
    
    /**
     * Delete a call from history
     * @param callId ID of the call to delete
     */
    suspend fun deleteCallFromHistory(callId: String): Result<Unit>
    
    /**
     * Clear all call history
     */
    suspend fun clearCallHistory(): Result<Unit>
    
    /**
     * Check if there's an active call
     * @return true if there's an active call
     */
    suspend fun hasActiveCall(): Result<Boolean>
    
    /**
     * Get the currently active call, if any
     * @return The active [Call] or null
     */
    suspend fun getActiveCall(): Result<Call?>
    
    /**
     * Get TURN/STUN server configuration
     * @return Map of ICE server configurations
     */
    suspend fun getIceServers(): Result<Map<String, String>>
}