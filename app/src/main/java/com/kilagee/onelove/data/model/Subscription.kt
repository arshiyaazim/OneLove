package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Enum representing subscription status
 */
enum class SubscriptionStatus {
    ACTIVE, CANCELED, EXPIRED, PAST_DUE, UNPAID, TRIALING
}

/**
 * Enum representing payment provider
 */
enum class PaymentProvider {
    STRIPE, GOOGLE_PAY, APPLE_PAY, PAYPAL, MANUAL
}

/**
 * Subscription data class for Firestore mapping
 */
data class Subscription(
    @DocumentId
    val id: String = "",
    
    // User info
    val userId: String = "",
    val userEmail: String = "",
    
    // Subscription details
    val name: String = "",
    val description: String = "",
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val priceId: String = "",
    val price: Double = 0.0,
    val currency: String = "USD",
    val interval: String = "month", // "month", "year", "week", "day"
    val intervalCount: Int = 1,
    
    // Dates
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val trialStart: Timestamp? = null,
    val trialEnd: Timestamp? = null,
    val canceledAt: Timestamp? = null,
    val currentPeriodStart: Timestamp? = null,
    val currentPeriodEnd: Timestamp? = null,
    
    // Renewal settings
    val autoRenew: Boolean = true,
    val cancelAtPeriodEnd: Boolean = false,
    
    // Payment info
    val customerId: String = "",
    val paymentMethodId: String? = null,
    val paymentProvider: PaymentProvider = PaymentProvider.STRIPE,
    val externalId: String? = null, // Stripe subscription ID, etc.
    val latestInvoiceId: String? = null,
    val latestReceiptUrl: String? = null,
    
    // Features
    val features: List<String> = emptyList(),
    val quotaLimits: Map<String, Int> = emptyMap(),
    val pointsIncluded: Int = 0,
    
    // Metadata
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if subscription is active
     */
    fun isActive(): Boolean {
        return status == SubscriptionStatus.ACTIVE || 
               status == SubscriptionStatus.TRIALING
    }
    
    /**
     * Check if subscription is in trial period
     */
    fun isInTrial(): Boolean {
        return status == SubscriptionStatus.TRIALING
    }
    
    /**
     * Get days remaining in current period
     */
    fun getDaysRemaining(): Int {
        return currentPeriodEnd?.let {
            val now = java.util.Date()
            val end = it.toDate()
            val diff = end.time - now.time
            return (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        } ?: 0
    }
    
    /**
     * Get days remaining in trial
     */
    fun getTrialDaysRemaining(): Int {
        return trialEnd?.let {
            val now = java.util.Date()
            val end = it.toDate()
            val diff = end.time - now.time
            return (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        } ?: 0
    }
    
    /**
     * Get formatted price
     */
    fun getFormattedPrice(): String {
        val currencySymbol = when (currency) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            else -> currency
        }
        
        val priceString = if (currency == "JPY") {
            price.toInt().toString()
        } else {
            String.format("%.2f", price)
        }
        
        val intervalString = when (interval) {
            "month" -> if (intervalCount == 1) "/month" else "/$intervalCount months"
            "year" -> if (intervalCount == 1) "/year" else "/$intervalCount years"
            "week" -> if (intervalCount == 1) "/week" else "/$intervalCount weeks"
            "day" -> if (intervalCount == 1) "/day" else "/$intervalCount days"
            else -> "/$interval"
        }
        
        return "$currencySymbol$priceString$intervalString"
    }
    
    /**
     * Get formatted status
     */
    fun getFormattedStatus(): String {
        return when (status) {
            SubscriptionStatus.ACTIVE -> "Active"
            SubscriptionStatus.CANCELED -> "Canceled"
            SubscriptionStatus.EXPIRED -> "Expired"
            SubscriptionStatus.PAST_DUE -> "Past Due"
            SubscriptionStatus.UNPAID -> "Unpaid"
            SubscriptionStatus.TRIALING -> "Trial"
        }
    }
    
    /**
     * Check if a feature is included in the subscription
     */
    fun hasFeature(feature: String): Boolean {
        return features.contains(feature)
    }
    
    /**
     * Get quota limit for a feature
     */
    fun getQuotaLimit(feature: String): Int {
        return quotaLimits[feature] ?: 0
    }
}