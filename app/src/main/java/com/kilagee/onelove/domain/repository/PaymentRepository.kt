package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Payment
import com.kilagee.onelove.data.model.PaymentMethodDetails
import com.kilagee.onelove.data.model.PointsPackage
import com.kilagee.onelove.data.model.PointsTransaction
import com.kilagee.onelove.data.model.Subscription
import com.kilagee.onelove.data.model.SubscriptionPeriod
import com.kilagee.onelove.data.model.SubscriptionType
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for payment and subscription operations
 */
interface PaymentRepository {
    
    /**
     * Get available subscription plans
     */
    suspend fun getSubscriptionPlans(): Result<List<SubscriptionPlan>>
    
    /**
     * Get points packages
     */
    suspend fun getPointsPackages(): Result<List<PointsPackage>>
    
    /**
     * Create a payment intent for subscription
     */
    suspend fun createSubscriptionPaymentIntent(
        userId: String,
        subscriptionType: SubscriptionType,
        period: SubscriptionPeriod
    ): Result<PaymentIntent>
    
    /**
     * Create a payment intent for points purchase
     */
    suspend fun createPointsPaymentIntent(
        userId: String,
        pointsPackageId: String
    ): Result<PaymentIntent>
    
    /**
     * Confirm a payment intent
     */
    suspend fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentMethodId: String
    ): Result<PaymentIntent>
    
    /**
     * Handle payment action (3D Secure, etc.)
     */
    suspend fun handlePaymentAction(
        paymentIntentId: String,
        actionData: Map<String, String>
    ): Result<PaymentIntent>
    
    /**
     * Get user's current subscription
     */
    suspend fun getUserSubscription(userId: String): Result<Subscription?>
    
    /**
     * Get user's current subscription as a flow
     */
    fun getUserSubscriptionFlow(userId: String): Flow<Result<Subscription?>>
    
    /**
     * Cancel a subscription
     */
    suspend fun cancelSubscription(userId: String): Result<Subscription>
    
    /**
     * Update subscription auto-renewal
     */
    suspend fun updateSubscriptionAutoRenewal(userId: String, autoRenew: Boolean): Result<Subscription>
    
    /**
     * Add payment method
     */
    suspend fun addPaymentMethod(
        userId: String,
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String,
        cardHolderName: String
    ): Result<PaymentMethodDetails>
    
    /**
     * Get user's payment methods
     */
    suspend fun getUserPaymentMethods(userId: String): Result<List<PaymentMethodDetails>>
    
    /**
     * Delete a payment method
     */
    suspend fun deletePaymentMethod(userId: String, paymentMethodId: String): Result<Unit>
    
    /**
     * Set default payment method
     */
    suspend fun setDefaultPaymentMethod(userId: String, paymentMethodId: String): Result<Unit>
    
    /**
     * Get payment history
     */
    suspend fun getPaymentHistory(userId: String): Result<List<Payment>>
    
    /**
     * Get points transactions
     */
    suspend fun getPointsTransactions(userId: String): Result<List<PointsTransaction>>
    
    /**
     * Get user points balance
     */
    suspend fun getUserPointsBalance(userId: String): Result<Int>
    
    /**
     * Get user points balance as a flow
     */
    fun getUserPointsBalanceFlow(userId: String): Flow<Result<Int>>
    
    /**
     * Add points to user
     */
    suspend fun addPointsToUser(
        userId: String,
        points: Int,
        description: String,
        referenceId: String = ""
    ): Result<Int> // Returns new balance
    
    /**
     * Deduct points from user
     */
    suspend fun deductPointsFromUser(
        userId: String,
        points: Int,
        description: String,
        referenceId: String = ""
    ): Result<Int> // Returns new balance
}

/**
 * Data class for subscription plans
 */
data class SubscriptionPlan(
    val id: String,
    val type: SubscriptionType,
    val period: SubscriptionPeriod,
    val price: Double,
    val currency: String,
    val description: String,
    val benefits: List<String>,
    val isPopular: Boolean = false
)

/**
 * Data class for payment intents
 */
data class PaymentIntent(
    val id: String,
    val clientSecret: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val requiresAction: Boolean = false,
    val actionData: Map<String, String> = mapOf()
)