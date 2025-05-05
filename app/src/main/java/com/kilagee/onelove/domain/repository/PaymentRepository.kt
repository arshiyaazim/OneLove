package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Payment
import com.kilagee.onelove.data.model.PaymentProvider
import com.kilagee.onelove.data.model.PaymentStatus
import com.kilagee.onelove.data.model.PaymentType
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for payment-related operations
 */
interface PaymentRepository {
    
    /**
     * Create a payment intent for purchase
     */
    fun createPaymentIntent(
        amountUsd: Double,
        type: PaymentType,
        subscriptionId: String? = null,
        offerId: String? = null,
        provider: PaymentProvider = PaymentProvider.STRIPE
    ): Flow<Resource<PaymentIntent>>
    
    /**
     * Confirm a payment intent with payment method
     */
    fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentMethodId: String
    ): Flow<Resource<Payment>>
    
    /**
     * Get all payments for the current user
     */
    fun getUserPayments(): Flow<Resource<List<Payment>>>
    
    /**
     * Get payments by status for the current user
     */
    fun getPaymentsByStatus(status: PaymentStatus): Flow<Resource<List<Payment>>>
    
    /**
     * Get payments by type for the current user
     */
    fun getPaymentsByType(type: PaymentType): Flow<Resource<List<Payment>>>
    
    /**
     * Get a payment by ID
     */
    fun getPaymentById(paymentId: String): Flow<Resource<Payment>>
    
    /**
     * Get payments for a subscription
     */
    fun getPaymentsForSubscription(subscriptionId: String): Flow<Resource<List<Payment>>>
    
    /**
     * Get payments for an offer
     */
    fun getPaymentsForOffer(offerId: String): Flow<Resource<List<Payment>>>
    
    /**
     * Get total spent by user in date range
     */
    fun getTotalSpentInDateRange(startDate: Date, endDate: Date): Flow<Resource<Double>>
    
    /**
     * Handle a payment that requires additional action (e.g., 3D Secure)
     */
    fun handlePaymentRequiringAction(
        paymentId: String, 
        actionResult: String?
    ): Flow<Resource<Payment>>
    
    /**
     * Request a refund for a payment
     */
    fun requestRefund(
        paymentId: String,
        reason: String? = null,
        amountUsd: Double? = null  // Null means full refund
    ): Flow<Resource<Payment>>
    
    /**
     * Save payment method for future use
     */
    fun savePaymentMethod(
        paymentMethodDetails: PaymentMethodDetails
    ): Flow<Resource<String>> // Returns payment method ID
    
    /**
     * Get saved payment methods for current user
     */
    fun getSavedPaymentMethods(): Flow<Resource<List<PaymentMethod>>>
    
    /**
     * Delete a saved payment method
     */
    fun deletePaymentMethod(paymentMethodId: String): Flow<Resource<Unit>>
    
    /**
     * Set a payment method as default
     */
    fun setDefaultPaymentMethod(paymentMethodId: String): Flow<Resource<Unit>>
    
    /**
     * Get payments requiring action from the user
     */
    fun getPaymentsRequiringAction(): Flow<Resource<List<Payment>>>
}

/**
 * Data class for payment intent
 */
data class PaymentIntent(
    val id: String,
    val clientSecret: String,
    val amountUsd: Double,
    val status: String,
    val requiresAction: Boolean,
    val paymentMethodTypes: List<String>,
    val currency: String = "USD"
)

/**
 * Data class for payment method details
 */
data class PaymentMethodDetails(
    val type: String, // e.g., "card", "bkash"
    val cardNumber: String? = null,
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val cvc: String? = null,
    val billingName: String? = null,
    val billingEmail: String? = null,
    val billingPhone: String? = null,
    val billingAddress: BillingAddress? = null,
    val mobileNumber: String? = null, // For mobile payment methods
    val saveForFutureUse: Boolean = false
)

/**
 * Data class for billing address
 */
data class BillingAddress(
    val line1: String,
    val line2: String? = null,
    val city: String,
    val state: String? = null,
    val postalCode: String,
    val country: String
)

/**
 * Data class for payment method
 */
data class PaymentMethod(
    val id: String,
    val type: String,
    val last4: String?, // Last 4 digits of card/phone
    val brand: String?, // Card brand
    val expiryMonth: Int?,
    val expiryYear: Int?,
    val holderName: String?,
    val isDefault: Boolean,
    val createdAt: Date
)