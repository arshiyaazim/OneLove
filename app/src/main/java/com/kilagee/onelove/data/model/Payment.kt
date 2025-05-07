package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

/**
 * Subscription data model
 * Represents a user's subscription to premium services
 */
@Parcelize
data class Subscription(
    @DocumentId val id: String = "",
    val userId: String = "",
    val planId: String = "",
    val planName: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val interval: String = INTERVAL_MONTHLY,
    val status: String = STATUS_INACTIVE,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val trialEnd: Timestamp? = null,
    val canceledAt: Timestamp? = null,
    val autoRenew: Boolean = true,
    val stripeSubscriptionId: String? = null,
    val stripeCustomerId: String? = null,
    val lastPaymentStatus: String? = null,
    val lastPaymentDate: Timestamp? = null,
    val lastPaymentAmount: Double? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val metadata: Map<String, Any>? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val INTERVAL_MONTHLY = "MONTHLY"
        const val INTERVAL_YEARLY = "YEARLY"
        
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_INACTIVE = "INACTIVE"
        const val STATUS_PAST_DUE = "PAST_DUE"
        const val STATUS_CANCELED = "CANCELED"
        const val STATUS_TRIALING = "TRIALING"
    }
}

/**
 * SubscriptionPlan data model
 * Represents a subscription plan that users can purchase
 */
@Parcelize
data class SubscriptionPlan(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val features: List<String> = emptyList(),
    val monthlyPrice: Double = 0.0,
    val yearlyPrice: Double = 0.0,
    val yearlyDiscount: Int = 0, // percentage
    val currency: String = "USD",
    val stripePriceIdMonthly: String = "",
    val stripePriceIdYearly: String = "",
    val trialDays: Int = 0,
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val icon: String? = null,
    val pointsIncluded: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
}

/**
 * Transaction data model
 * Represents a financial transaction in the app
 */
@Parcelize
data class Transaction(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = TYPE_SUBSCRIPTION,
    val amount: Double = 0.0,
    val currency: String = "USD",
    val description: String = "",
    val status: String = STATUS_PENDING,
    val paymentMethod: String? = null,
    val paymentMethodDetails: Map<String, Any>? = null,
    val timestamp: Timestamp? = null,
    val subscriptionId: String? = null,
    val pointsAmount: Int? = null,
    val pointsPurchaseId: String? = null,
    val paymentIntentId: String? = null,
    val chargeId: String? = null,
    val failureReason: String? = null,
    val receiptUrl: String? = null,
    val metadata: Map<String, Any>? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
    
    companion object {
        const val TYPE_SUBSCRIPTION = "SUBSCRIPTION"
        const val TYPE_POINTS_PURCHASE = "POINTS_PURCHASE"
        const val TYPE_POINTS_REDEMPTION = "POINTS_REDEMPTION"
        const val TYPE_OFFER_PURCHASE = "OFFER_PURCHASE"
        const val TYPE_FEATURE_PURCHASE = "FEATURE_PURCHASE"
        
        const val STATUS_PENDING = "PENDING"
        const val STATUS_SUCCEEDED = "SUCCEEDED"
        const val STATUS_FAILED = "FAILED"
        const val STATUS_REFUNDED = "REFUNDED"
        const val STATUS_CANCELED = "CANCELED"
    }
}

/**
 * PointsPackage data model
 * Represents a points package that users can purchase
 */
@Parcelize
data class PointsPackage(
    @DocumentId val id: String = "",
    val name: String = "",
    val pointsAmount: Int = 0,
    val price: Double = 0.0,
    val currency: String = "USD",
    val stripePriceId: String = "",
    val bonusPoints: Int = 0,
    val description: String = "",
    val isPopular: Boolean = false,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable {
    
    // For Firestore data conversion
    constructor() : this(id = "")
}