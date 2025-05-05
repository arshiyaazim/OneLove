package com.kilagee.onelove.data.database.dao

import androidx.room.*
import com.kilagee.onelove.data.model.Offer
import com.kilagee.onelove.data.model.OfferStatus
import com.kilagee.onelove.data.model.OfferType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OfferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: Offer)
    
    @Update
    suspend fun updateOffer(offer: Offer)
    
    @Delete
    suspend fun deleteOffer(offer: Offer)
    
    @Query("SELECT * FROM offers WHERE id = :offerId")
    fun getOfferById(offerId: String): Flow<Offer?>
    
    @Query("SELECT * FROM offers WHERE sender_id = :userId ORDER BY created_at DESC")
    fun getSentOffers(userId: String): Flow<List<Offer>>
    
    @Query("SELECT * FROM offers WHERE receiver_id = :userId ORDER BY created_at DESC")
    fun getReceivedOffers(userId: String): Flow<List<Offer>>
    
    @Query("SELECT * FROM offers WHERE receiver_id = :userId AND status = :status ORDER BY created_at DESC")
    fun getOffersByReceiverAndStatus(userId: String, status: OfferStatus): Flow<List<Offer>>
    
    @Query("SELECT * FROM offers WHERE sender_id = :userId AND status = :status ORDER BY created_at DESC")
    fun getOffersBySenderAndStatus(userId: String, status: OfferStatus): Flow<List<Offer>>
    
    @Query("UPDATE offers SET status = :status, updated_at = :updatedAt WHERE id = :offerId")
    suspend fun updateOfferStatus(offerId: String, status: OfferStatus, updatedAt: Date)
    
    @Query("SELECT * FROM offers WHERE type = :type AND (status = :status1 OR status = :status2) ORDER BY created_at DESC LIMIT :limit")
    fun getOffersByTypeAndStatus(type: OfferType, status1: OfferStatus, status2: OfferStatus, limit: Int): Flow<List<Offer>>
    
    @Query("SELECT COUNT(*) FROM offers WHERE receiver_id = :userId AND status = :status")
    fun getPendingOffersCount(userId: String, status: OfferStatus = OfferStatus.PENDING): Flow<Int>
}