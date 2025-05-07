package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Call
import com.kilagee.onelove.data.model.CallEndReason
import com.kilagee.onelove.data.model.CallStatus
import com.kilagee.onelove.data.model.CallType
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for call operations
 */
interface CallRepository {
    
    /**
     * Initiate a call
     */
    suspend fun initiateCall(
        matchId: String,
        callerId: String,
        receiverId: String,
        type: CallType
    ): Result<Call>
    
    /**
     * Get a call by ID
     */
    suspend fun getCallById(callId: String): Result<Call>
    
    /**
     * Get call as a flow for real-time updates
     */
    fun getCallFlow(callId: String): Flow<Result<Call>>
    
    /**
     * Update call status
     */
    suspend fun updateCallStatus(callId: String, status: CallStatus): Result<Unit>
    
    /**
     * Accept a call
     */
    suspend fun acceptCall(callId: String): Result<Unit>
    
    /**
     * Decline a call
     */
    suspend fun declineCall(callId: String, reason: CallEndReason = CallEndReason.NORMAL): Result<Unit>
    
    /**
     * End a call
     */
    suspend fun endCall(
        callId: String,
        endTime: Date = Date(),
        reason: CallEndReason = CallEndReason.NORMAL
    ): Result<Unit>
    
    /**
     * Get call history for a match
     */
    suspend fun getCallHistoryForMatch(matchId: String): Result<List<Call>>
    
    /**
     * Get call history for a user
     */
    suspend fun getCallHistoryForUser(userId: String): Result<List<Call>>
    
    /**
     * Get active calls for a user
     */
    suspend fun getActiveCallsForUser(userId: String): Result<List<Call>>
    
    /**
     * Check if user is in an active call
     */
    suspend fun isUserInActiveCall(userId: String): Result<Boolean>
    
    /**
     * Update call quality rating
     */
    suspend fun updateCallQuality(callId: String, quality: Int): Result<Unit>
    
    /**
     * Upload call snapshot (for video calls)
     */
    suspend fun uploadCallSnapshot(callId: String, imageBase64: String): Result<String> // Returns URL
    
    /**
     * Get WebRTC signaling data as a flow
     */
    fun getSignalingDataFlow(callId: String): Flow<Result<Map<String, Any>>>
    
    /**
     * Send WebRTC signaling data
     */
    suspend fun sendSignalingData(callId: String, data: Map<String, Any>): Result<Unit>
}