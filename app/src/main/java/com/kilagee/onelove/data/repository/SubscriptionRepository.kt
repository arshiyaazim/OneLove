package com.kilagee.onelove.data.repository

import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.Subscription
import com.kilagee.onelove.data.model.SubscriptionStatus
import com.kilagee.onelove.data.model.SubscriptionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for subscriptions
 */
interface SubscriptionRepository {
    
    /**
     * Get subscription by ID
     * @param subscriptionId Subscription ID
     * @return Result containing the subscription or error
     */
    suspend fun getSubscriptionById(subscriptionId: String): Result<Subscription>
    
    /**
     * Get subscription by ID as Flow
     * @param subscriptionId Subscription ID
     * @return Flow emitting Result containing the subscription or error
     */
    fun getSubscriptionByIdFlow(subscriptionId: String): Flow<Result<Subscription>>
    
    /**
     * Get user's active subscription
     * @param userId User ID
     * @return Result containing the subscription or error
     */
    suspend fun getUserActiveSubscription(userId: String): Result<Subscription?>
    
    /**
     * Get user's active subscription as Flow
     * @param userId User ID
     * @return Flow emitting Result containing the subscription or error
     */
    fun getUserActiveSubscriptionFlow(userId: String): Flow<Result<Subscription?>>
    
    /**
     * Get all user subscriptions
     * @param userId User ID
     * @param limit Maximum number of results
     * @return Result containing list of subscriptions or error
     */
    suspend fun getUserSubscriptions(userId: String, limit: Int = 10): Result<List<Subscription>>
    
    /**
     * Create subscription
     * @param userId User ID
     * @param subscriptionType Subscription type
     * @param paymentMethod Payment method
     * @param stripeCustomerId Stripe customer ID
     * @param stripeSubscriptionId Stripe subscription ID
     * @param startDate Start date
     * @param endDate End date
     * @param autoRenew Auto-renew setting
     * @param paymentAmount Payment amount
     * @param paymentCurrency Payment currency
     * @param featuresIncluded List of included features
     * @return Result containing the subscription ID or error
     */
    suspend fun createSubscription(
        userId: String,
        subscriptionType: SubscriptionType,
        paymentMethod: String,
        stripeCustomerId: String,
        stripeSubscriptionId: String,
        startDate: Date,
        endDate: Date,
        autoRenew: Boolean,
        paymentAmount: Double,
        paymentCurrency: String = "USD",
        featuresIncluded: List<String> = emptyList()
    ): Result<String>
    
    /**
     * Cancel subscription
     * @param subscriptionId Subscription ID
     * @param cancellationReason Optional cancellation reason
     * @return Result containing the updated subscription or error
     */
    suspend fun cancelSubscription(
        subscriptionId: String,
        cancellationReason: String? = null
    ): Result<Subscription>
    
    /**
     * Update subscription auto-renew setting
     * @param subscriptionId Subscription ID
     * @param autoRenew Auto-renew setting
     * @return Result containing the updated subscription or error
     */
    suspend fun updateSubscriptionAutoRenew(
        subscriptionId: String,
        autoRenew: Boolean
    ): Result<Subscription>
    
    /**
     * Change subscription type
     * @param subscriptionId Subscription ID
     * @param newType New subscription type
     * @return Result containing the updated subscription or error
     */
    suspend fun changeSubscriptionType(
        subscriptionId: String,
        newType: SubscriptionType
    ): Result<Subscription>
    
    /**
     * Extend subscription
     * @param subscriptionId Subscription ID
     * @param newEndDate New end date
     * @return Result containing the updated subscription or error
     */
    suspend fun extendSubscription(
        subscriptionId: String,
        newEndDate: Date
    ): Result<Subscription>
    
    /**
     * Update subscription status
     * @param subscriptionId Subscription ID
     * @param status New status
     * @return Result containing the updated subscription or error
     */
    suspend fun updateSubscriptionStatus(
        subscriptionId: String,
        status: SubscriptionStatus
    ): Result<Subscription>
    
    /**
     * Get available subscription plans
     * @return Result containing list of subscription plans or error
     */
    suspend fun getSubscriptionPlans(): Result<List<Map<String, Any>>>
    
    /**
     * Sync subscription data with Stripe
     * @param userId User ID
     * @return Result containing the updated subscription or error
     */
    suspend fun syncUserSubscriptionWithStripe(userId: String): Result<Subscription?>
    
    /**
     * Check if user has premium features
     * @param userId User ID
     * @return Result containing boolean indicating premium status or error
     */
    suspend fun userHasPremiumFeatures(userId: String): Result<Boolean>
    
    /**
     * Get user subscription statistics
     * @return Result containing subscription statistics or error
     */
    suspend fun getSubscriptionStatistics(): Result<Map<String, Int>>
    
    /**
     * Get expiring subscriptions
     * @param daysUntilExpiry Days until expiry
     * @param limit Maximum number of results
     * @return Result containing list of expiring subscriptions or error
     */
    suspend fun getExpiringSubscriptions(
        daysUntilExpiry: Int = 7,
        limit: Int = 100
    ): Result<List<Subscription>>
}