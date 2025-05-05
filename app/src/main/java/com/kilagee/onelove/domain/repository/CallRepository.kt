package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Call
import com.kilagee.onelove.data.model.CallStatus
import com.kilagee.onelove.data.model.CallType
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for call-related operations
 */
interface CallRepository {
    
    /**
     * Create a new one-to-one call
     */
    fun createCall(receiverId: String, type: CallType): Flow<Resource<Call>>
    
    /**
     * Create a new group call
     */
    fun createGroupCall(participantIds: List<String>, type: CallType): Flow<Resource<Call>>
    
    /**
     * Get a call by ID
     */
    fun getCall(callId: String): Flow<Resource<Call>>
    
    /**
     * Get all calls for the current user
     */
    fun getCalls(): Flow<Resource<List<Call>>>
    
    /**
     * Get call history with a specific user
     */
    fun getCallHistoryWithUser(userId: String): Flow<Resource<List<Call>>>
    
    /**
     * Update call status
     */
    fun updateCallStatus(callId: String, status: CallStatus): Flow<Resource<Call>>
    
    /**
     * Answer an incoming call
     */
    fun answerCall(callId: String): Flow<Resource<Call>>
    
    /**
     * Decline an incoming call
     */
    fun declineCall(callId: String): Flow<Resource<Call>>
    
    /**
     * End an ongoing call
     */
    fun endCall(callId: String, endTime: Date, duration: Long): Flow<Resource<Call>>
    
    /**
     * Get Agora RTC token for a call
     */
    fun getAgoraToken(callId: String, userId: String, channelName: String): Flow<Resource<String>>
    
    /**
     * Delete a call record
     */
    fun deleteCall(callId: String): Flow<Resource<Unit>>
}