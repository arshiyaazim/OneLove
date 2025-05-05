package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kilagee.onelove.data.database.converter.DateConverter
import java.util.Date

/**
 * Enum for subscription types
 */
enum class SubscriptionType {
    BASIC,          // Free tier
    BOOST,          // Profile boosting to get more visibility
    UNLIMITED,      // Unlimited offers and interactions
    PREMIUM         // All features included
}

/**
 * Enum for subscription status
 */
enum class SubscriptionStatus {
    ACTIVE,         // Currently active subscription
    CANCELED,       // Will be canceled at the end of the billing period
    EXPIRED,        // No longer active
    PENDING         // Payment pending
}

/**
 * Enum for payment provider
 */
enum class PaymentProvider {
    STRIPE,
    RAZORPAY,
    BKASH,
    GOOGLE_PAY
}

/**
 * Entity class for subscriptions
 */
@Entity(tableName = "subscriptions")
@TypeConverters(DateConverter::class)
data class Subscription(
    @PrimaryKey
    val id: String,
    
    // User who owns this subscription
    val userId: String,
    
    // Type of subscription
    val type: SubscriptionType,
    
    // Current status
    val status: SubscriptionStatus,
    
    // Payment provider used
    val paymentProvider: PaymentProvider,
    
    // Payment provider's subscription ID for recurring billing
    val providerSubscriptionId: String?,
    
    // Monthly price in USD
    val priceUsd: Double,
    
    // Start date of the subscription
    val startDate: Date,
    
    // End date of the current billing period
    val currentPeriodEnd: Date,
    
    // Whether it renews automatically
    val autoRenew: Boolean,
    
    // Date when the subscription was cancelled (if applicable)
    val canceledAt: Date?,
    
    // JSON string of included features
    val features: String,
    
    // Creation timestamp
    val createdAt: Date,
    
    // Last updated timestamp
    val updatedAt: Date
)