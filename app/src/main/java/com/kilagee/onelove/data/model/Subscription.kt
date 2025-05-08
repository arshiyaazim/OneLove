package com.kilagee.onelove.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Data class representing a subscription plan
 */
@Parcelize
data class SubscriptionPlan(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val currency: String = "USD",
    val interval: BillingInterval = BillingInterval.MONTH,
    val intervalCount: Int = 1, // e.g., 1 month, 3 months, 6 months
    val trialPeriodDays: Int? = null,
    val features: List<String> = emptyList(),
    val stripePriceId: String? = null,
    val isPopular: Boolean = false,
    val isActive: Boolean = true,
    val discount: Double = 0.0, // Percentage discount
    val metadata: Map<String, String> = emptyMap()
) : Parcelable {
    
    /**
     * Convert to a map for Firestore
     */
    fun toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        result["id"] = id
        result["name"] = name
        result["description"] = description
        result["price"] = price
        result["currency"] = currency
        result["interval"] = interval.name
        result["intervalCount"] = intervalCount
        result["trialPeriodDays"] = trialPeriodDays
        result["features"] = features
        result["stripePriceId"] = stripePriceId
        result["isPopular"] = isPopular
        result["isActive"] = isActive
        result["discount"] = discount
        result["metadata"] = metadata
        return result
    }
    
    companion object {
        /**
         * Create from Firestore document
         */
        fun fromMap(map: Map<String, Any?>, id: String): SubscriptionPlan {
            return SubscriptionPlan(
                id = id,
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                currency = map["currency"] as? String ?: "USD",
                interval = try {
                    BillingInterval.valueOf(map["interval"] as? String ?: BillingInterval.MONTH.name)
                } catch (e: IllegalArgumentException) {
                    BillingInterval.MONTH
                },
                intervalCount = (map["intervalCount"] as? Number)?.toInt() ?: 1,
                trialPeriodDays = (map["trialPeriodDays"] as? Number)?.toInt(),
                features = (map["features"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                stripePriceId = map["stripePriceId"] as? String,
                isPopular = map["isPopular"] as? Boolean ?: false,
                isActive = map["isActive"] as? Boolean ?: true,
                discount = (map["discount"] as? Number)?.toDouble() ?: 0.0,
                metadata = (map["metadata"] as? Map<*, *>)?.mapNotNull { 
                    if (it.key is String && it.value is String) {
                        it.key as String to it.value as String
                    } else {
                        null
                    }
                }?.toMap() ?: emptyMap()
            )
        }
    }
}

/**
 * Enum for billing intervals
 */
enum class BillingInterval {
    DAY,
    WEEK,
    MONTH,
    YEAR
}

/**
 * Data class representing a subscription
 */
@Parcelize
data class Subscription(
    val id: String = "",
    val userId: String = "",
    val planId: String = "",
    val stripePlanId: String? = null,
    val stripeSubscriptionId: String? = null,
    val status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val startDate: Date = Date(),
    val endDate: Date? = null,
    val cancelDate: Date? = null,
    val cancelReason: String? = null,
    val autoRenew: Boolean = true,
    val trialEnd: Date? = null,
    val price: Double = 0.0,
    val currency: String = "USD",
    val paymentMethodId: String? = null,
    val invoiceId: String? = null,
    val couponCode: String? = null,
    val discountAmount: Double = 0.0,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable {
    
    /**
     * Convert to a map for Firestore
     */
    fun toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        result["id"] = id
        result["userId"] = userId
        result["planId"] = planId
        result["stripePlanId"] = stripePlanId
        result["stripeSubscriptionId"] = stripeSubscriptionId
        result["status"] = status.name
        result["startDate"] = Timestamp(startDate)
        result["endDate"] = endDate?.let { Timestamp(it) }
        result["cancelDate"] = cancelDate?.let { Timestamp(it) }
        result["cancelReason"] = cancelReason
        result["autoRenew"] = autoRenew
        result["trialEnd"] = trialEnd?.let { Timestamp(it) }
        result["price"] = price
        result["currency"] = currency
        result["paymentMethodId"] = paymentMethodId
        result["invoiceId"] = invoiceId
        result["couponCode"] = couponCode
        result["discountAmount"] = discountAmount
        result["metadata"] = metadata
        result["createdAt"] = Timestamp(createdAt)
        result["updatedAt"] = Timestamp(updatedAt)
        return result
    }
    
    companion object {
        /**
         * Create from Firestore document
         */
        fun fromMap(map: Map<String, Any?>, id: String): Subscription {
            return Subscription(
                id = id,
                userId = map["userId"] as? String ?: "",
                planId = map["planId"] as? String ?: "",
                stripePlanId = map["stripePlanId"] as? String,
                stripeSubscriptionId = map["stripeSubscriptionId"] as? String,
                status = try {
                    SubscriptionStatus.valueOf(map["status"] as? String ?: SubscriptionStatus.ACTIVE.name)
                } catch (e: IllegalArgumentException) {
                    SubscriptionStatus.ACTIVE
                },
                startDate = (map["startDate"] as? Timestamp)?.toDate() ?: Date(),
                endDate = (map["endDate"] as? Timestamp)?.toDate(),
                cancelDate = (map["cancelDate"] as? Timestamp)?.toDate(),
                cancelReason = map["cancelReason"] as? String,
                autoRenew = map["autoRenew"] as? Boolean ?: true,
                trialEnd = (map["trialEnd"] as? Timestamp)?.toDate(),
                price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                currency = map["currency"] as? String ?: "USD",
                paymentMethodId = map["paymentMethodId"] as? String,
                invoiceId = map["invoiceId"] as? String,
                couponCode = map["couponCode"] as? String,
                discountAmount = (map["discountAmount"] as? Number)?.toDouble() ?: 0.0,
                metadata = (map["metadata"] as? Map<*, *>)?.mapNotNull { 
                    if (it.key is String && it.value is String) {
                        it.key as String to it.value as String
                    } else {
                        null
                    }
                }?.toMap() ?: emptyMap(),
                createdAt = (map["createdAt"] as? Timestamp)?.toDate() ?: Date(),
                updatedAt = (map["updatedAt"] as? Timestamp)?.toDate() ?: Date()
            )
        }
    }
}

/**
 * Enum for subscription status
 */
enum class SubscriptionStatus {
    ACTIVE,
    TRIALING,
    PAST_DUE,
    CANCELED,
    UNPAID,
    INCOMPLETE,
    INCOMPLETE_EXPIRED,
    PAUSED
}

/**
 * Data class representing a payment method
 */
@Parcelize
data class PaymentMethod(
    val id: String = "",
    val userId: String = "",
    val type: PaymentMethodType = PaymentMethodType.CARD,
    val last4: String = "",
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val brand: String? = null,
    val isDefault: Boolean = false,
    val stripePaymentMethodId: String? = null,
    val createdAt: Date = Date()
) : Parcelable {
    
    /**
     * Convert to a map for Firestore
     */
    fun toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        result["id"] = id
        result["userId"] = userId
        result["type"] = type.name
        result["last4"] = last4
        result["expiryMonth"] = expiryMonth
        result["expiryYear"] = expiryYear
        result["brand"] = brand
        result["isDefault"] = isDefault
        result["stripePaymentMethodId"] = stripePaymentMethodId
        result["createdAt"] = Timestamp(createdAt)
        return result
    }
    
    companion object {
        /**
         * Create from Firestore document
         */
        fun fromMap(map: Map<String, Any?>, id: String): PaymentMethod {
            return PaymentMethod(
                id = id,
                userId = map["userId"] as? String ?: "",
                type = try {
                    PaymentMethodType.valueOf(map["type"] as? String ?: PaymentMethodType.CARD.name)
                } catch (e: IllegalArgumentException) {
                    PaymentMethodType.CARD
                },
                last4 = map["last4"] as? String ?: "",
                expiryMonth = (map["expiryMonth"] as? Number)?.toInt(),
                expiryYear = (map["expiryYear"] as? Number)?.toInt(),
                brand = map["brand"] as? String,
                isDefault = map["isDefault"] as? Boolean ?: false,
                stripePaymentMethodId = map["stripePaymentMethodId"] as? String,
                createdAt = (map["createdAt"] as? Timestamp)?.toDate() ?: Date()
            )
        }
    }
}

/**
 * Enum for payment method types
 */
enum class PaymentMethodType {
    CARD,
    BANK_ACCOUNT,
    PAYPAL,
    GOOGLE_PAY,
    APPLE_PAY
}

/**
 * Data class representing a transaction
 */
@Parcelize
data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.PAYMENT,
    val amount: Double = 0.0,
    val currency: String = "USD",
    val status: TransactionStatus = TransactionStatus.SUCCESSFUL,
    val paymentMethodId: String? = null,
    val subscriptionId: String? = null,
    val invoiceId: String? = null,
    val stripePaymentIntentId: String? = null,
    val description: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Date = Date()
) : Parcelable {
    
    /**
     * Convert to a map for Firestore
     */
    fun toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        result["id"] = id
        result["userId"] = userId
        result["type"] = type.name
        result["amount"] = amount
        result["currency"] = currency
        result["status"] = status.name
        result["paymentMethodId"] = paymentMethodId
        result["subscriptionId"] = subscriptionId
        result["invoiceId"] = invoiceId
        result["stripePaymentIntentId"] = stripePaymentIntentId
        result["description"] = description
        result["metadata"] = metadata
        result["createdAt"] = Timestamp(createdAt)
        return result
    }
    
    companion object {
        /**
         * Create from Firestore document
         */
        fun fromMap(map: Map<String, Any?>, id: String): Transaction {
            return Transaction(
                id = id,
                userId = map["userId"] as? String ?: "",
                type = try {
                    TransactionType.valueOf(map["type"] as? String ?: TransactionType.PAYMENT.name)
                } catch (e: IllegalArgumentException) {
                    TransactionType.PAYMENT
                },
                amount = (map["amount"] as? Number)?.toDouble() ?: 0.0,
                currency = map["currency"] as? String ?: "USD",
                status = try {
                    TransactionStatus.valueOf(map["status"] as? String ?: TransactionStatus.SUCCESSFUL.name)
                } catch (e: IllegalArgumentException) {
                    TransactionStatus.SUCCESSFUL
                },
                paymentMethodId = map["paymentMethodId"] as? String,
                subscriptionId = map["subscriptionId"] as? String,
                invoiceId = map["invoiceId"] as? String,
                stripePaymentIntentId = map["stripePaymentIntentId"] as? String,
                description = map["description"] as? String,
                metadata = (map["metadata"] as? Map<*, *>)?.mapNotNull { 
                    if (it.key is String && it.value is String) {
                        it.key as String to it.value as String
                    } else {
                        null
                    }
                }?.toMap() ?: emptyMap(),
                createdAt = (map["createdAt"] as? Timestamp)?.toDate() ?: Date()
            )
        }
    }
}

/**
 * Enum for transaction types
 */
enum class TransactionType {
    PAYMENT,
    REFUND,
    CREDIT,
    POINTS_PURCHASE,
    SUBSCRIPTION_PAYMENT,
    FEATURE_PURCHASE
}

/**
 * Enum for transaction status
 */
enum class TransactionStatus {
    PENDING,
    SUCCESSFUL,
    FAILED,
    REFUNDED,
    CANCELED
}