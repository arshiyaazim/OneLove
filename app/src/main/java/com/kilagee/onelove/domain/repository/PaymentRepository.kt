package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.PaymentMethod
import com.kilagee.onelove.data.model.SubscriptionPlan
import com.kilagee.onelove.data.model.SubscriptionStatus
import com.kilagee.onelove.data.model.Transaction
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for payment and subscription operations
 */
interface PaymentRepository {
    /**
     * Get available subscription plans
     * @return Flow of Result containing a list of subscription plans or an error
     */
    fun getSubscriptionPlans(): Flow<Result<List<SubscriptionPlan>>>
    
    /**
     * Get the current user's subscription status
     * @return Flow of Result containing the subscription status or an error
     */
    fun getSubscriptionStatus(): Flow<Result<SubscriptionStatus>>
    
    /**
     * Create a payment intent for a subscription purchase
     * @param planId ID of the subscription plan
     * @param paymentMethodId Optional payment method ID to use
     * @return Result containing the client secret for the payment intent or an error
     */
    suspend fun createSubscriptionPaymentIntent(
        planId: String,
        paymentMethodId: String? = null
    ): Result<String>
    
    /**
     * Confirm a payment intent
     * @param paymentIntentId ID of the payment intent to confirm
     * @param paymentMethodId ID of the payment method to use
     * @return Result containing the updated payment intent status or an error
     */
    suspend fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentMethodId: String
    ): Result<String>
    
    /**
     * Handle additional action required for payment
     * @param paymentIntentId ID of the payment intent
     * @param action Action to perform
     * @return Result containing the updated payment intent status or an error
     */
    suspend fun handlePaymentAction(
        paymentIntentId: String,
        action: String
    ): Result<String>
    
    /**
     * Subscribe to a plan
     * @param planId ID of the subscription plan
     * @param paymentMethodId ID of the payment method to use
     * @return Result containing the subscription ID or an error
     */
    suspend fun subscribe(planId: String, paymentMethodId: String): Result<String>
    
    /**
     * Cancel the current subscription
     * @param atPeriodEnd Whether to cancel at the end of the billing period
     * @return Result indicating success or failure
     */
    suspend fun cancelSubscription(atPeriodEnd: Boolean = true): Result<Unit>
    
    /**
     * Update subscription auto-renew settings
     * @param autoRenew Whether to auto-renew the subscription
     * @return Result indicating success or failure
     */
    suspend fun updateSubscriptionAutoRenew(autoRenew: Boolean): Result<Unit>
    
    /**
     * Get the user's saved payment methods
     * @return Flow of Result containing a list of payment methods or an error
     */
    fun getPaymentMethods(): Flow<Result<List<PaymentMethod>>>
    
    /**
     * Add a new payment method
     * @param paymentMethodId ID of the payment method to add
     * @param isDefault Whether this should be the default payment method
     * @return Result containing the added payment method or an error
     */
    suspend fun addPaymentMethod(
        paymentMethodId: String,
        isDefault: Boolean = false
    ): Result<PaymentMethod>
    
    /**
     * Remove a payment method
     * @param paymentMethodId ID of the payment method to remove
     * @return Result indicating success or failure
     */
    suspend fun removePaymentMethod(paymentMethodId: String): Result<Unit>
    
    /**
     * Set a payment method as default
     * @param paymentMethodId ID of the payment method to set as default
     * @return Result indicating success or failure
     */
    suspend fun setDefaultPaymentMethod(paymentMethodId: String): Result<Unit>
    
    /**
     * Get transaction history
     * @param limit Maximum number of transactions to return
     * @param offset Pagination offset
     * @return Flow of Result containing a list of transactions or an error
     */
    fun getTransactionHistory(
        limit: Int = 20,
        offset: Int = 0
    ): Flow<Result<List<Transaction>>>
    
    /**
     * Purchase coins (in-app currency)
     * @param packageId ID of the coin package to purchase
     * @param paymentMethodId ID of the payment method to use
     * @return Result containing the transaction or an error
     */
    suspend fun purchaseCoins(
        packageId: String,
        paymentMethodId: String
    ): Result<Transaction>
    
    /**
     * Get the current coin balance
     * @return Flow of Result containing the coin balance or an error
     */
    fun getCoinBalance(): Flow<Result<Int>>
    
    /**
     * Use coins for a feature
     * @param feature Feature to use coins for
     * @param amount Amount of coins to use
     * @return Result indicating success or failure
     */
    suspend fun useCoins(feature: String, amount: Int): Result<Int>
}