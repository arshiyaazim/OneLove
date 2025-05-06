package com.kilagee.onelove.data.repository

import android.net.Uri
import com.kilagee.onelove.data.model.GeoPoint
import com.kilagee.onelove.data.model.Offer
import com.kilagee.onelove.data.model.OfferStatus
import com.kilagee.onelove.data.model.OfferType
import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.User
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for offers
 */
interface OfferRepository {
    
    /**
     * Get offer by ID
     * @param offerId Offer ID
     * @return Result containing the offer or error
     */
    suspend fun getOfferById(offerId: String): Result<Offer>
    
    /**
     * Get offer by ID as Flow
     * @param offerId Offer ID
     * @return Flow emitting Result containing the offer or error
     */
    fun getOfferByIdFlow(offerId: String): Flow<Result<Offer>>
    
    /**
     * Create offer
     * @param senderId Sender user ID
     * @param receiverId Receiver user ID
     * @param title Offer title
     * @param description Offer description
     * @param offerType Offer type
     * @param pointValue Point value of the offer
     * @param mediaUris Optional list of media URIs
     * @param location Optional location
     * @param scheduledTime Optional scheduled time
     * @param expiryTime Optional expiry time
     * @param terms Optional list of terms
     * @param isPremiumOffer Whether it's a premium offer
     * @return Result containing the created offer ID or error
     */
    suspend fun createOffer(
        senderId: String,
        receiverId: String,
        title: String,
        description: String,
        offerType: OfferType,
        pointValue: Int,
        mediaUris: List<Uri>? = null,
        location: GeoPoint? = null,
        scheduledTime: Date? = null,
        expiryTime: Date? = null,
        terms: List<String>? = null,
        isPremiumOffer: Boolean = false
    ): Result<String>
    
    /**
     * Get offers sent by user
     * @param userId User ID
     * @param status Optional filter by status
     * @param limit Maximum number of results
     * @return Result containing list of offers or error
     */
    suspend fun getOffersSentByUser(
        userId: String,
        status: OfferStatus? = null,
        limit: Int = 50
    ): Result<List<Offer>>
    
    /**
     * Get offers sent by user as Flow
     * @param userId User ID
     * @param status Optional filter by status
     * @return Flow emitting Result containing list of offers or error
     */
    fun getOffersSentByUserFlow(
        userId: String,
        status: OfferStatus? = null
    ): Flow<Result<List<Offer>>>
    
    /**
     * Get offers received by user
     * @param userId User ID
     * @param status Optional filter by status
     * @param limit Maximum number of results
     * @return Result containing list of offers or error
     */
    suspend fun getOffersReceivedByUser(
        userId: String,
        status: OfferStatus? = null,
        limit: Int = 50
    ): Result<List<Offer>>
    
    /**
     * Get offers received by user as Flow
     * @param userId User ID
     * @param status Optional filter by status
     * @return Flow emitting Result containing list of offers or error
     */
    fun getOffersReceivedByUserFlow(
        userId: String,
        status: OfferStatus? = null
    ): Flow<Result<List<Offer>>>
    
    /**
     * Accept offer
     * @param offerId Offer ID
     * @return Result containing the updated offer or error
     */
    suspend fun acceptOffer(offerId: String): Result<Offer>
    
    /**
     * Reject offer
     * @param offerId Offer ID
     * @param reason Optional rejection reason
     * @return Result containing the updated offer or error
     */
    suspend fun rejectOffer(offerId: String, reason: String? = null): Result<Offer>
    
    /**
     * Cancel offer
     * @param offerId Offer ID
     * @return Result containing the updated offer or error
     */
    suspend fun cancelOffer(offerId: String): Result<Offer>
    
    /**
     * Create counter offer
     * @param originalOfferId Original offer ID
     * @param title Counter offer title
     * @param description Counter offer description
     * @param pointValue Counter offer point value
     * @param mediaUris Optional list of media URIs
     * @param scheduledTime Optional scheduled time
     * @param terms Optional list of terms
     * @return Result containing the counter offer ID or error
     */
    suspend fun createCounterOffer(
        originalOfferId: String,
        title: String,
        description: String,
        pointValue: Int,
        mediaUris: List<Uri>? = null,
        scheduledTime: Date? = null,
        terms: List<String>? = null
    ): Result<String>
    
    /**
     * Mark offer as completed
     * @param offerId Offer ID
     * @param evidenceUris Optional list of completion evidence URIs
     * @return Result containing the updated offer or error
     */
    suspend fun completeOffer(
        offerId: String,
        evidenceUris: List<Uri>? = null
    ): Result<Offer>
    
    /**
     * Get all offers between users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Result containing list of offers or error
     */
    suspend fun getOffersBetweenUsers(userId1: String, userId2: String): Result<List<Offer>>
    
    /**
     * Get offers by type
     * @param userId User ID
     * @param offerType Offer type
     * @param limit Maximum number of results
     * @return Result containing list of offers or error
     */
    suspend fun getOffersByType(
        userId: String,
        offerType: OfferType,
        limit: Int = 50
    ): Result<List<Offer>>
    
    /**
     * Get featured offers
     * @param limit Maximum number of results
     * @return Result containing list of offers or error
     */
    suspend fun getFeaturedOffers(limit: Int = 20): Result<List<Offer>>
    
    /**
     * Set offer as featured
     * @param offerId Offer ID
     * @param isFeatured Featured status
     * @return Result containing the updated offer or error
     */
    suspend fun setOfferFeaturedStatus(offerId: String, isFeatured: Boolean): Result<Offer>
    
    /**
     * Update offer terms
     * @param offerId Offer ID
     * @param terms New terms
     * @return Result containing the updated offer or error
     */
    suspend fun updateOfferTerms(offerId: String, terms: List<String>): Result<Offer>
    
    /**
     * Get pending offers count for user
     * @param userId User ID
     * @return Result containing the count or error
     */
    suspend fun getPendingOffersCount(userId: String): Result<Int>
    
    /**
     * Get pending offers count for user as Flow
     * @param userId User ID
     * @return Flow emitting Result containing the count or error
     */
    fun getPendingOffersCountFlow(userId: String): Flow<Result<Int>>
    
    /**
     * Get users with most accepted offers
     * @param limit Maximum number of results
     * @return Result containing list of users or error
     */
    suspend fun getUsersWithMostAcceptedOffers(limit: Int = 10): Result<List<User>>
}