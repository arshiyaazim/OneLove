package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kilagee.onelove.data.database.converter.DateConverter
import java.util.Date

/**
 * Enum for payment status
 */
enum class PaymentStatus {
    PENDING,       // Payment is initiated but not completed
    SUCCEEDED,     // Payment was successful
    FAILED,        // Payment failed
    REFUNDED       // Payment was refunded
}

/**
 * Enum for payment type
 */
enum class PaymentType {
    SUBSCRIPTION,  // Payment for subscription
    ONE_TIME,      // One-time purchase (e.g., coins, boost)
    OFFER,         // Payment for an offer
    REFUND         // Refund transaction
}

/**
 * Entity class for payments
 */
@Entity(tableName = "payments")
@TypeConverters(DateConverter::class)
data class Payment(
    @PrimaryKey
    val id: String,
    
    // User who made the payment
    val userId: String,
    
    // Amount in USD
    val amountUsd: Double,
    
    // Amount in local currency
    val amountLocal: Double,
    
    // Currency code (e.g., USD, EUR)
    val currency: String,
    
    // Payment status
    val status: PaymentStatus,
    
    // Payment type
    val type: PaymentType,
    
    // Payment provider
    val provider: PaymentProvider,
    
    // Provider's payment ID
    val providerPaymentId: String,
    
    // Provider's transaction ID
    val providerTransactionId: String?,
    
    // Subscription ID if this payment is for a subscription
    val subscriptionId: String?,
    
    // Offer ID if this payment is for an offer
    val offerId: String?,
    
    // Whether the payment requires additional action from the user
    val requiresAction: Boolean,
    
    // URL for additional actions (e.g., 3D Secure)
    val actionUrl: String?,
    
    // Receipt URL or ID
    val receiptUrl: String?,
    
    // Creation timestamp
    val createdAt: Date,
    
    // Last updated timestamp
    val updatedAt: Date
)