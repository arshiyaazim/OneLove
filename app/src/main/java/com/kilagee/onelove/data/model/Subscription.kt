package com.kilagee.onelove.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * Data model for user subscriptions
 */
data class Subscription(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val stripeCustomerId: String? = null,
    val stripeSubscriptionId: String? = null,
    val startDate: Date = Date(),
    val endDate: Date? = null,
    val isActive: Boolean = false,
    val autoRenew: Boolean = false,
    val paymentMethod: PaymentMethod? = null,
    val features: List<String> = emptyList(),
    val priceId: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val interval: String = "month",
    val cancelReason: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    /**
     * Subscription tiers with their features
     */
    enum class SubscriptionTier {
        FREE, BASIC, PREMIUM, VIP;
        
        fun getFeatures(): List<String> {
            return when (this) {
                FREE -> listOf(
                    "Limited Swipes", 
                    "Basic Matching", 
                    "Text Messaging"
                )
                BASIC -> listOf(
                    "Unlimited Swipes",
                    "Enhanced Matching",
                    "Text & Image Messaging",
                    "1 Rewind per day",
                    "See who liked you"
                )
                PREMIUM -> listOf(
                    "All Basic Features",
                    "Unlimited Rewinds",
                    "Video Calls",
                    "Audio Messages",
                    "Priority Matching",
                    "Ad-Free Experience",
                    "AI Chat Suggestions",
                    "5 Profile Boosts monthly"
                )
                VIP -> listOf(
                    "All Premium Features",
                    "Unlimited Profile Boosts",
                    "Priority Support",
                    "Exclusive AI Profiles",
                    "Custom Profile Badge",
                    "Advanced Matching Algorithms",
                    "Read Receipts",
                    "Incognito Mode"
                )
            }
        }
        
        fun getMonthlyPrice(): Double {
            return when (this) {
                FREE -> 0.0
                BASIC -> 9.99
                PREMIUM -> 19.99
                VIP -> 39.99
            }
        }
        
        fun getYearlyPrice(): Double {
            val monthlyPrice = getMonthlyPrice()
            // 20% discount for yearly subscription
            return monthlyPrice * 12 * 0.8
        }
    }
    
    /**
     * Payment method details
     */
    data class PaymentMethod(
        val type: String = "card",
        val lastFour: String = "",
        val expiryMonth: Int = 0,
        val expiryYear: Int = 0,
        val brand: String = "",
        val isDefault: Boolean = true
    )
}