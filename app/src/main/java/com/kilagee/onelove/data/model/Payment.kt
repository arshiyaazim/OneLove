package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing payment status
 */
enum class PaymentStatus {
    PENDING, SUCCEEDED, FAILED, REFUNDED, CANCELED
}

/**
 * Enum representing payment type
 */
enum class PaymentType {
    SUBSCRIPTION, POINTS, FEATURE, OFFER, GIFT, OTHER
}

/**
 * Payment data class for Firestore mapping
 */
data class Payment(
    @DocumentId
    val id: String = "",
    
    // User info
    val userId: String = "",
    val userEmail: String? = null,
    
    // Payment details
    val amount: Double = 0.0,
    val currency: String = "USD",
    val status: PaymentStatus = PaymentStatus.PENDING,
    val paymentType: PaymentType = PaymentType.OTHER,
    val description: String = "",
    
    // Provider info
    val provider: PaymentProvider = PaymentProvider.STRIPE,
    val paymentIntentId: String? = null,
    val paymentMethodId: String? = null,
    val customerId: String? = null,
    val invoiceId: String? = null,
    val receiptUrl: String? = null,
    val chargeId: String? = null,
    
    // Related entities
    val subscriptionId: String? = null,
    val offerId: String? = null,
    val pointsAmount: Int? = null,
    val featureId: String? = null,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    val processedAt: Timestamp? = null,
    val refundedAt: Timestamp? = null,
    val canceledAt: Timestamp? = null,
    
    // Additional info
    val notes: String? = null,
    val errorMessage: String? = null,
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if payment is pending
     */
    fun isPending(): Boolean {
        return status == PaymentStatus.PENDING
    }
    
    /**
     * Check if payment succeeded
     */
    fun isSuccessful(): Boolean {
        return status == PaymentStatus.SUCCEEDED
    }
    
    /**
     * Check if payment failed
     */
    fun isFailed(): Boolean {
        return status == PaymentStatus.FAILED
    }
    
    /**
     * Get formatted amount
     */
    fun getFormattedAmount(): String {
        val currencySymbol = when (currency) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            else -> currency
        }
        
        val amountString = if (currency == "JPY") {
            amount.toInt().toString()
        } else {
            String.format("%.2f", amount)
        }
        
        return "$currencySymbol$amountString"
    }
    
    /**
     * Get formatted status
     */
    fun getFormattedStatus(): String {
        return when (status) {
            PaymentStatus.PENDING -> "Pending"
            PaymentStatus.SUCCEEDED -> "Successful"
            PaymentStatus.FAILED -> "Failed"
            PaymentStatus.REFUNDED -> "Refunded"
            PaymentStatus.CANCELED -> "Canceled"
        }
    }
    
    /**
     * Get formatted payment type
     */
    fun getFormattedPaymentType(): String {
        return when (paymentType) {
            PaymentType.SUBSCRIPTION -> "Subscription"
            PaymentType.POINTS -> "Points Purchase"
            PaymentType.FEATURE -> "Feature Unlock"
            PaymentType.OFFER -> "Offer Purchase"
            PaymentType.GIFT -> "Gift"
            PaymentType.OTHER -> "Other"
        }
    }
    
    /**
     * Get formatted date
     */
    fun getFormattedDate(): String {
        val date = createdAt?.toDate() ?: return ""
        val formatter = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Get formatted time
     */
    fun getFormattedTime(): String {
        val date = createdAt?.toDate() ?: return ""
        val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        return formatter.format(date)
    }
}