package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Subscription
import com.kilagee.onelove.data.model.SubscriptionStatus
import com.kilagee.onelove.data.model.SubscriptionType
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for subscription-related operations
 */
interface SubscriptionRepository {
    
    /**
     * Get available subscription plans
     */
    fun getSubscriptionPlans(): Flow<Resource<List<SubscriptionPlan>>>
    
    /**
     * Create a subscription for the current user
     */
    fun createSubscription(
        type: SubscriptionType, 
        paymentMethodId: String,
        autoRenew: Boolean
    ): Flow<Resource<Subscription>>
    
    /**
     * Update a subscription
     */
    fun updateSubscription(
        subscriptionId: String,
        status: SubscriptionStatus? = null,
        autoRenew: Boolean? = null
    ): Flow<Resource<Subscription>>
    
    /**
     * Cancel a subscription
     */
    fun cancelSubscription(
        subscriptionId: String,
        cancelImmediately: Boolean = false
    ): Flow<Resource<Subscription>>
    
    /**
     * Get all subscriptions for the current user
     */
    fun getUserSubscriptions(): Flow<Resource<List<Subscription>>>
    
    /**
     * Get active subscriptions for the current user
     */
    fun getActiveSubscriptions(): Flow<Resource<List<Subscription>>>
    
    /**
     * Get a subscription by ID
     */
    fun getSubscriptionById(subscriptionId: String): Flow<Resource<Subscription>>
    
    /**
     * Check if user has an active subscription of any type
     */
    fun hasActiveSubscription(): Flow<Resource<Boolean>>
    
    /**
     * Check if user has an active subscription of specific type
     */
    fun hasActiveSubscriptionOfType(type: SubscriptionType): Flow<Resource<Boolean>>
    
    /**
     * Get the current active subscription of the user (highest tier if multiple)
     */
    fun getCurrentActiveSubscription(): Flow<Resource<Subscription?>>
    
    /**
     * Update payment method for a subscription
     */
    fun updateSubscriptionPaymentMethod(
        subscriptionId: String,
        paymentMethodId: String
    ): Flow<Resource<Subscription>>
    
    /**
     * Sync subscriptions with the payment provider
     */
    fun syncSubscriptions(): Flow<Resource<Unit>>
}

/**
 * Data class representing a subscription plan
 */
data class SubscriptionPlan(
    val type: SubscriptionType,
    val name: String,
    val description: String,
    val priceUsd: Double,
    val features: List<String>,
    val durationMonths: Int,
    val popular: Boolean = false
)