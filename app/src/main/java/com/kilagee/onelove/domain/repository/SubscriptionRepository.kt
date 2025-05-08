package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.PaymentMethod
import com.kilagee.onelove.data.model.Subscription
import com.kilagee.onelove.data.model.SubscriptionPlan
import com.kilagee.onelove.data.model.Transaction
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Interface for subscription-related operations
 */
interface SubscriptionRepository {
    
    /**
     * Get available subscription plans
     * @return List of [SubscriptionPlan] objects
     */
    suspend fun getSubscriptionPlans(): Result<List<SubscriptionPlan>>
    
    /**
     * Get a specific subscription plan
     * @param planId ID of the plan to retrieve
     * @return The [SubscriptionPlan] object
     */
    suspend fun getSubscriptionPlan(planId: String): Result<SubscriptionPlan>
    
    /**
     * Get user's current subscription
     * @return The current [Subscription] or null if not subscribed
     */
    suspend fun getCurrentSubscription(): Result<Subscription?>
    
    /**
     * Get user's subscription history
     * @return List of [Subscription] objects
     */
    suspend fun getSubscriptionHistory(): Result<List<Subscription>>
    
    /**
     * Check if user has an active subscription
     * @return true if subscribed, false otherwise
     */
    suspend fun checkActiveSubscription(): Result<Boolean>
    
    /**
     * Subscribe to a plan
     * @param planId ID of the plan to subscribe to
     * @param paymentMethodId ID of the payment method to use
     * @param startDate Optional start date for the subscription
     * @return The created [Subscription]
     */
    suspend fun subscribe(
        planId: String,
        paymentMethodId: String,
        startDate: Date? = null
    ): Result<Subscription>
    
    /**
     * Initiate purchase of a subscription
     * @param planId ID of the plan to purchase
     * @return Client secret for Stripe payment intent
     */
    suspend fun initiatePurchase(planId: String): Result<String>
    
    /**
     * Complete subscription purchase
     * @param paymentIntentId Stripe payment intent ID
     * @return The created [Subscription]
     */
    suspend fun completePurchase(paymentIntentId: String): Result<Subscription>
    
    /**
     * Cancel subscription
     * @param subscriptionId ID of the subscription to cancel
     * @param cancelImmediately Whether to cancel immediately or at the end of the billing period
     * @param reason Optional reason for cancellation
     * @return Success result
     */
    suspend fun cancelSubscription(
        subscriptionId: String,
        cancelImmediately: Boolean = false,
        reason: String? = null
    ): Result<Unit>
    
    /**
     * Pause subscription
     * @param subscriptionId ID of the subscription to pause
     * @param resumeDate Optional date to automatically resume the subscription
     * @return Success result
     */
    suspend fun pauseSubscription(
        subscriptionId: String,
        resumeDate: Date? = null
    ): Result<Unit>
    
    /**
     * Resume subscription
     * @param subscriptionId ID of the subscription to resume
     * @return Success result
     */
    suspend fun resumeSubscription(subscriptionId: String): Result<Unit>
    
    /**
     * Change subscription plan
     * @param subscriptionId ID of the subscription to change
     * @param newPlanId ID of the new plan
     * @param prorated Whether to prorate the cost
     * @return The updated [Subscription]
     */
    suspend fun changeSubscriptionPlan(
        subscriptionId: String,
        newPlanId: String,
        prorated: Boolean = true
    ): Result<Subscription>
    
    /**
     * Get user's payment methods
     * @return List of [PaymentMethod] objects
     */
    suspend fun getPaymentMethods(): Result<List<PaymentMethod>>
    
    /**
     * Add a payment method
     * @param paymentMethodId Stripe payment method ID
     * @param isDefault Whether this is the default payment method
     * @return The added [PaymentMethod]
     */
    suspend fun addPaymentMethod(
        paymentMethodId: String,
        isDefault: Boolean = false
    ): Result<PaymentMethod>
    
    /**
     * Remove a payment method
     * @param paymentMethodId ID of the payment method to remove
     * @return Success result
     */
    suspend fun removePaymentMethod(paymentMethodId: String): Result<Unit>
    
    /**
     * Set default payment method
     * @param paymentMethodId ID of the payment method to set as default
     * @return Success result
     */
    suspend fun setDefaultPaymentMethod(paymentMethodId: String): Result<Unit>
    
    /**
     * Get transaction history
     * @param limit Maximum number of transactions to fetch
     * @return List of [Transaction] objects
     */
    suspend fun getTransactionHistory(limit: Int = 20): Result<List<Transaction>>
    
    /**
     * Get transaction
     * @param transactionId ID of the transaction to retrieve
     * @return The [Transaction] object
     */
    suspend fun getTransaction(transactionId: String): Result<Transaction>
    
    /**
     * Get subscription updates as a flow
     * @return Flow of [Subscription] objects
     */
    fun getSubscriptionUpdatesFlow(): Flow<Result<Subscription?>>
    
    /**
     * Get setup intent for adding a payment method
     * @return Client secret for Stripe setup intent
     */
    suspend fun getSetupIntent(): Result<String>
    
    /**
     * Apply a coupon to a subscription
     * @param subscriptionId ID of the subscription
     * @param couponCode Coupon code to apply
     * @return The updated [Subscription]
     */
    suspend fun applyCoupon(
        subscriptionId: String,
        couponCode: String
    ): Result<Subscription>
    
    /**
     * Validate a coupon code
     * @param couponCode Coupon code to validate
     * @return Discount amount if valid, null otherwise
     */
    suspend fun validateCoupon(couponCode: String): Result<Double?>
}