package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Offer
import com.kilagee.onelove.data.model.OfferStatus
import com.kilagee.onelove.data.model.OfferType
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for offers functionality
 */
interface OfferRepository {
    
    /**
     * Create a new offer
     * 
     * @param receiverId ID of the user receiving the offer
     * @param type Type of offer
     * @param title Title of the offer
     * @param description Description of the offer
     * @param location Location of the offer (if applicable)
     * @param proposedTime Proposed time for the offer (if applicable)
     * @param pointsOffered Points offered (if applicable)
     * @return Flow of Resource containing the created Offer
     */
    fun createOffer(
        receiverId: String,
        type: OfferType,
        title: String,
        description: String = "",
        location: String = "",
        proposedTime: Date? = null,
        pointsOffered: Int = 0
    ): Flow<Resource<Offer>>
    
    /**
     * Get offers sent by the current user
     * 
     * @return Flow of Resource containing a list of sent offers
     */
    fun getSentOffers(): Flow<Resource<List<Offer>>>
    
    /**
     * Get offers received by the current user
     * 
     * @return Flow of Resource containing a list of received offers
     */
    fun getReceivedOffers(): Flow<Resource<List<Offer>>>
    
    /**
     * Get all offers for the current user (both sent and received)
     * 
     * @return Flow of Resource containing a list of all offers
     */
    fun getAllOffers(): Flow<Resource<List<Offer>>>
    
    /**
     * Get offers by status for the current user
     * 
     * @param status Status of offers to retrieve
     * @param sent Whether to get sent offers (true) or received offers (false)
     * @return Flow of Resource containing a list of offers with the specified status
     */
    fun getOffersByStatus(status: OfferStatus, sent: Boolean = false): Flow<Resource<List<Offer>>>
    
    /**
     * Get a specific offer by ID
     * 
     * @param offerId ID of the offer to retrieve
     * @return Flow of Resource containing the offer
     */
    fun getOfferById(offerId: String): Flow<Resource<Offer>>
    
    /**
     * Update the status of an offer
     * 
     * @param offerId ID of the offer to update
     * @param status New status of the offer
     * @return Flow of Resource indicating success/failure
     */
    fun updateOfferStatus(offerId: String, status: OfferStatus): Flow<Resource<Unit>>
    
    /**
     * Get the count of pending offers for the current user
     * 
     * @return Flow of Resource containing the count
     */
    fun getPendingOffersCount(): Flow<Resource<Int>>
}