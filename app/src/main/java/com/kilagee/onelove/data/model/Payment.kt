package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Represents a payment made by a user
 */
@Parcelize
data class Payment(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val type: PaymentType = PaymentType.SUBSCRIPTION,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val provider: PaymentProvider = PaymentProvider.STRIPE,
    val providerPaymentId: String = "", // Payment ID from the provider (e.g., Stripe)
    val description: String = "",
    val subscriptionId: String = "", // ID of the subscription if type is SUBSCRIPTION
    val pointsPackageId: String = "", // ID of the points package if type is POINTS
    val pointsAmount: Int = 0, // Number of points purchased if type is POINTS
    val receiptUrl: String = "", // URL to payment receipt
    val paymentMethod: PaymentMethod = PaymentMethod.CREDIT_CARD,
    val paymentMethodDetails: PaymentMethodDetails = PaymentMethodDetails(),
    val metadata: Map<String, String> = mapOf(), // Additional payment metadata
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

@Parcelize
data class PaymentMethodDetails(
    val cardBrand: String = "", // e.g., "visa", "mastercard"
    val last4: String = "", // Last 4 digits of the card
    val expiryMonth: Int = 0,
    val expiryYear: Int = 0,
    val country: String = "",
    val name: String = "", // Cardholder name
    val isDefault: Boolean = false
) : Parcelable

@Parcelize
data class Subscription(
    val id: String = "",
    val userId: String = "",
    val type: SubscriptionType = SubscriptionType.PREMIUM,
    val status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val startDate: Date = Date(),
    val endDate: Date? = null,
    val renewalDate: Date? = null,
    val canceledAt: Date? = null,
    val period: SubscriptionPeriod = SubscriptionPeriod.MONTHLY,
    val price: Double = 0.0,
    val currency: String = "USD",
    val autoRenew: Boolean = true,
    val providerSubscriptionId: String = "", // Subscription ID from payment provider
    val providerCustomerId: String = "", // Customer ID from payment provider
    val paymentId: String = "", // Latest payment ID
    val trialEndDate: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

@Parcelize
data class PointsPackage(
    val id: String = "",
    val name: String = "",
    val points: Int = 0,
    val bonusPoints: Int = 0,
    val price: Double = 0.0,
    val currency: String = "USD",
    val isPopular: Boolean = false,
    val isBestValue: Boolean = false,
    val isActive: Boolean = true,
    val iconUrl: String = "",
    val displayOrder: Int = 0
) : Parcelable

@Parcelize
data class PointsTransaction(
    val id: String = "",
    val userId: String = "",
    val amount: Int = 0, // Positive for additions, negative for deductions
    val type: PointsTransactionType = PointsTransactionType.PURCHASE,
    val description: String = "",
    val referenceId: String = "", // ID of related entity (payment, offer, etc.)
    val balanceAfter: Int = 0,
    val timestamp: Date = Date()
) : Parcelable

enum class PaymentType {
    SUBSCRIPTION, POINTS, ONE_TIME
}

enum class PaymentStatus {
    PENDING, SUCCEEDED, FAILED, REFUNDED, CANCELED
}

enum class PaymentProvider {
    STRIPE, PAYPAL, GOOGLE_PAY, APPLE_PAY, OTHER
}

enum class PaymentMethod {
    CREDIT_CARD, DEBIT_CARD, GOOGLE_PAY, APPLE_PAY, PAYPAL, OTHER
}

enum class SubscriptionType {
    PREMIUM, GOLD, PLATINUM // Different tiers of subscription
}

enum class SubscriptionPeriod {
    MONTHLY, YEARLY
}

enum class PointsTransactionType {
    PURCHASE, // Bought points
    REWARD, // Earned through activities
    OFFER, // Spent or earned through offers
    GIFT, // Received as gift
    EXPIRY, // Points expired
    ADJUSTMENT, // Manual adjustment by admin
    REFUND // Refunded points
}