package com.kilagee.onelove.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Enum representing subscription status
 */
enum class SubscriptionStatus {
    ACTIVE, CANCELED, EXPIRED, PAUSED, PENDING, PAYMENT_FAILED, TRIALING
}

/**
 * Enum representing subscription purchase platforms
 */
enum class PurchasePlatform {
    GOOGLE_PLAY, APPLE_APP_STORE, STRIPE, OTHER
}

/**
 * Enum representing subscription renewal frequency
 */
enum class RenewalFrequency {
    MONTHLY, QUARTERLY, ANNUALLY, LIFETIME
}

/**
 * Enum representing subscription payment methods
 */
enum class PaymentMethod {
    CREDIT_CARD, DEBIT_CARD, PAYPAL, GOOGLE_PAY, APPLE_PAY, OTHER
}

/**
 * Sealed class representing subscription plan features
 */
sealed class SubscriptionPlanFeature {
    data class MessageLimit(val messageCount: Int) : SubscriptionPlanFeature()
    data class BoostCredits(val boostCount: Int, val refreshFrequency: String) : SubscriptionPlanFeature()
    data class VideoCallDuration(val maxMinutes: Int) : SubscriptionPlanFeature()
    data class LikeLimit(val likeCount: Int, val refreshFrequency: String) : SubscriptionPlanFeature()
    data class OfferLimit(val offerCount: Int, val refreshFrequency: String) : SubscriptionPlanFeature()
    data class AIChatCredits(val creditCount: Int, val refreshFrequency: String) : SubscriptionPlanFeature()
    data class AdvancedFilters(val enabled: Boolean) : SubscriptionPlanFeature()
    data class IncognitoMode(val enabled: Boolean) : SubscriptionPlanFeature()
    data class SeeWhoLikedYou(val enabled: Boolean) : SubscriptionPlanFeature()
    data class PriorityMatching(val enabled: Boolean) : SubscriptionPlanFeature()
    data class RemoveAds(val enabled: Boolean) : SubscriptionPlanFeature()
}

/**
 * Subscription plan data class
 */
data class SubscriptionPlan(
    val id: String = "",
    val name: String = "",
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val price: Double = 0.0,
    val currency: String = "USD",
    val renewalFrequency: RenewalFrequency = RenewalFrequency.MONTHLY,
    val trialDays: Int = 0,
    val description: String = "",
    val features: List<String> = emptyList(),
    val displayOrder: Int = 0,
    val stripePriceId: String? = null,
    val googleProductId: String? = null,
    val appleProductId: String? = null,
    val isActive: Boolean = true,
    val isPromoted: Boolean = false,
    val promotionalText: String? = null,
    val discountPercentage: Int = 0,
    
    // Features as structured data 
    @get:PropertyName("featureData")
    @set:PropertyName("featureData")
    var featureData: Map<String, Any> = emptyMap()
) {
    /**
     * Get typed features from serialized map
     */
    @Exclude
    fun getTypedFeatures(): List<SubscriptionPlanFeature> {
        val result = mutableListOf<SubscriptionPlanFeature>()
        
        featureData["messageLimit"]?.let {
            val count = (it as? Number)?.toInt() ?: 0
            if (count > 0) {
                result.add(SubscriptionPlanFeature.MessageLimit(count))
            }
        }
        
        featureData["boostCredits"]?.let { data ->
            when (data) {
                is Map<*, *> -> {
                    val count = (data["count"] as? Number)?.toInt() ?: 0
                    val refresh = (data["refreshFrequency"] as? String) ?: "monthly"
                    if (count > 0) {
                        result.add(SubscriptionPlanFeature.BoostCredits(count, refresh))
                    }
                }
                is Number -> {
                    val count = data.toInt()
                    if (count > 0) {
                        result.add(SubscriptionPlanFeature.BoostCredits(count, "monthly"))
                    }
                }
            }
        }
        
        featureData["videoCallDuration"]?.let {
            val minutes = (it as? Number)?.toInt() ?: 0
            if (minutes > 0) {
                result.add(SubscriptionPlanFeature.VideoCallDuration(minutes))
            }
        }
        
        featureData["likeLimit"]?.let { data ->
            when (data) {
                is Map<*, *> -> {
                    val count = (data["count"] as? Number)?.toInt() ?: 0
                    val refresh = (data["refreshFrequency"] as? String) ?: "daily"
                    if (count > 0) {
                        result.add(SubscriptionPlanFeature.LikeLimit(count, refresh))
                    }
                }
                is Number -> {
                    val count = data.toInt()
                    if (count > 0) {
                        result.add(SubscriptionPlanFeature.LikeLimit(count, "daily"))
                    }
                }
            }
        }
        
        featureData["offerLimit"]?.let { data ->
            when (data) {
                is Map<*, *> -> {
                    val count = (data["count"] as? Number)?.toInt() ?: 0
                    val refresh = (data["refreshFrequency"] as? String) ?: "weekly"
                    if (count > 0) {
                        result.add(SubscriptionPlanFeature.OfferLimit(count, refresh))
                    }
                }
                is Number -> {
                    val count = data.toInt()
                    if (count > 0) {
                        result.add(SubscriptionPlanFeature.OfferLimit(count, "weekly"))
                    }
                }
            }
        }
        
        featureData["aiChatCredits"]?.let { data ->
            when (data) {
                is Map<*, *> -> {
                    val count = (data["count"] as? Number)?.toInt() ?: 0
                    val refresh = (data["refreshFrequency"] as? String) ?: "daily"
                    if (count > 0) {
                        result.add(SubscriptionPlanFeature.AIChatCredits(count, refresh))
                    }
                }
                is Number -> {
                    val count = data.toInt()
                    if (count > 0) {
                        result.add(SubscriptionPlanFeature.AIChatCredits(count, "daily"))
                    }
                }
            }
        }
        
        featureData["advancedFilters"]?.let {
            val enabled = when (it) {
                is Boolean -> it
                is Number -> it.toInt() != 0
                is String -> it.toLowerCase() == "true" || it == "1"
                else -> false
            }
            result.add(SubscriptionPlanFeature.AdvancedFilters(enabled))
        }
        
        featureData["incognitoMode"]?.let {
            val enabled = when (it) {
                is Boolean -> it
                is Number -> it.toInt() != 0
                is String -> it.toLowerCase() == "true" || it == "1"
                else -> false
            }
            result.add(SubscriptionPlanFeature.IncognitoMode(enabled))
        }
        
        featureData["seeWhoLikedYou"]?.let {
            val enabled = when (it) {
                is Boolean -> it
                is Number -> it.toInt() != 0
                is String -> it.toLowerCase() == "true" || it == "1"
                else -> false
            }
            result.add(SubscriptionPlanFeature.SeeWhoLikedYou(enabled))
        }
        
        featureData["priorityMatching"]?.let {
            val enabled = when (it) {
                is Boolean -> it
                is Number -> it.toInt() != 0
                is String -> it.toLowerCase() == "true" || it == "1"
                else -> false
            }
            result.add(SubscriptionPlanFeature.PriorityMatching(enabled))
        }
        
        featureData["removeAds"]?.let {
            val enabled = when (it) {
                is Boolean -> it
                is Number -> it.toInt() != 0
                is String -> it.toLowerCase() == "true" || it == "1"
                else -> false
            }
            result.add(SubscriptionPlanFeature.RemoveAds(enabled))
        }
        
        return result
    }
    
    /**
     * Calculate price per month for comparison
     */
    @Exclude
    fun getPricePerMonth(): Double {
        return when (renewalFrequency) {
            RenewalFrequency.MONTHLY -> price
            RenewalFrequency.QUARTERLY -> price / 3
            RenewalFrequency.ANNUALLY -> price / 12
            RenewalFrequency.LIFETIME -> 0.0 // One-time payment
        }
    }
    
    /**
     * Get formatted price with currency
     */
    @Exclude
    fun getFormattedPrice(): String {
        return when (currency) {
            "USD" -> "$${price}"
            "EUR" -> "€${price}"
            "GBP" -> "£${price}"
            else -> "${price} $currency"
        }
    }
    
    /**
     * Get billing frequency text
     */
    @Exclude
    fun getBillingFrequencyText(): String {
        return when (renewalFrequency) {
            RenewalFrequency.MONTHLY -> "per month"
            RenewalFrequency.QUARTERLY -> "every 3 months"
            RenewalFrequency.ANNUALLY -> "per year"
            RenewalFrequency.LIFETIME -> "one-time payment"
        }
    }
    
    /**
     * Check if plan has a specific feature
     */
    @Exclude
    fun hasFeature(featureName: String): Boolean {
        return features.contains(featureName) || featureData.containsKey(featureName)
    }
}

/**
 * User subscription data class
 */
data class Subscription(
    @DocumentId
    val id: String = "",
    
    // User info
    val userId: String = "",
    val userEmail: String? = null,
    
    // Subscription details
    val planId: String = "",
    val planName: String = "",
    val planTier: SubscriptionTier = SubscriptionTier.FREE,
    val status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val autoRenew: Boolean = true,
    val renewalFrequency: RenewalFrequency = RenewalFrequency.MONTHLY,
    
    // Payment info
    val amount: Double = 0.0,
    val currency: String = "USD",
    val platform: PurchasePlatform = PurchasePlatform.STRIPE,
    val paymentMethod: PaymentMethod = PaymentMethod.CREDIT_CARD,
    val paymentMethodDetails: Map<String, Any> = emptyMap(),
    
    // External IDs
    val stripeSubscriptionId: String? = null,
    val stripeCustomerId: String? = null,
    val googlePurchaseToken: String? = null,
    val applePurchaseToken: String? = null,
    val transactionId: String? = null,
    
    // Timestamps
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val canceledAt: Timestamp? = null,
    val trialEndDate: Timestamp? = null,
    val nextBillingDate: Timestamp? = null,
    
    // Receipt and verification
    val receipt: String? = null,
    val isVerified: Boolean = false,
    
    // Promo and discounts
    val promoCode: String? = null,
    val discountPercentage: Int = 0,
    val discountAmount: Double = 0.0,
    
    // Tracking and analytics
    val cancelReason: String? = null,
    val referralCode: String? = null,
    val acquisitionChannel: String? = null,
    
    // Additional data
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if subscription is active
     */
    @Exclude
    fun isActive(): Boolean {
        return status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.TRIALING
    }
    
    /**
     * Check if subscription is in trial period
     */
    @Exclude
    fun isInTrial(): Boolean {
        if (status != SubscriptionStatus.TRIALING) return false
        
        val now = Timestamp.now()
        return trialEndDate?.let { it.compareTo(now) > 0 } ?: false
    }
    
    /**
     * Get days remaining in subscription
     */
    @Exclude
    fun getDaysRemaining(): Int {
        if (!isActive()) return 0
        
        val now = Timestamp.now().toDate().time
        val end = endDate?.toDate()?.time ?: return 0
        
        return ((end - now) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }
    
    /**
     * Get days remaining in trial
     */
    @Exclude
    fun getTrialDaysRemaining(): Int {
        if (!isInTrial()) return 0
        
        val now = Timestamp.now().toDate().time
        val trialEnd = trialEndDate?.toDate()?.time ?: return 0
        
        return ((trialEnd - now) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }
    
    /**
     * Get subscription duration in days
     */
    @Exclude
    fun getDurationInDays(): Int {
        val start = startDate?.toDate()?.time ?: return 0
        val end = endDate?.toDate()?.time ?: return 0
        
        return ((end - start) / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * Get next billing date text
     */
    @Exclude
    fun getNextBillingDateText(): String {
        if (!autoRenew) return "Auto-renewal disabled"
        if (status != SubscriptionStatus.ACTIVE && status != SubscriptionStatus.TRIALING) return "Subscription not active"
        
        val next = nextBillingDate?.toDate()
        return next?.let {
            val formatter = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            "Next billing: ${formatter.format(it)}"
        } ?: "Next billing date unavailable"
    }
    
    /**
     * Check if subscription is expiring soon (within 3 days)
     */
    @Exclude
    fun isExpiringSoon(): Boolean {
        return isActive() && getDaysRemaining() <= 3
    }
    
    /**
     * Get formatted transaction amount with currency
     */
    @Exclude
    fun getFormattedAmount(): String {
        return when (currency) {
            "USD" -> "$${amount}"
            "EUR" -> "€${amount}"
            "GBP" -> "£${amount}"
            else -> "${amount} $currency"
        }
    }
    
    /**
     * Get effective amount after discount
     */
    @Exclude
    fun getEffectiveAmount(): Double {
        if (discountPercentage > 0) {
            return amount * (1 - discountPercentage / 100.0)
        }
        return amount - discountAmount
    }
}