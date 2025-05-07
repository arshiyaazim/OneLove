package com.kilagee.onelove.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Notification model for app notifications
 */
@Parcelize
data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: NotificationType = NotificationType.GENERAL,
    val isRead: Boolean = false,
    val readAt: Date? = null,
    val createdAt: Date = Date(),
    val senderUserId: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String = "",
    val actionType: NotificationActionType = NotificationActionType.NONE,
    val actionId: String = "",
    val additionalData: Map<String, String> = emptyMap(),
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val deepLink: String = "",
    val imageUrl: String = "",
    val groupId: String = "",
    val expiresAt: Date? = null,
    val isSystemNotification: Boolean = false
) : Parcelable

/**
 * Notification type enum
 */
@Parcelize
enum class NotificationType : Parcelable {
    GENERAL,
    MATCH,
    MESSAGE,
    LIKE,
    OFFER,
    CALL,
    VERIFICATION,
    PAYMENT,
    SUBSCRIPTION,
    PROMOTION,
    PROFILE_VISIT,
    SYSTEM
}

/**
 * Notification action type enum
 */
@Parcelize
enum class NotificationActionType : Parcelable {
    NONE,
    OPEN_CHAT,
    OPEN_PROFILE,
    OPEN_MATCH,
    OPEN_OFFER,
    OPEN_CALL,
    OPEN_VERIFICATION,
    OPEN_PAYMENT,
    OPEN_SUBSCRIPTION,
    OPEN_APP
}

/**
 * Notification priority enum
 */
@Parcelize
enum class NotificationPriority : Parcelable {
    LOW,
    NORMAL,
    HIGH
}

/**
 * Notification settings for a user
 */
@Parcelize
data class NotificationSettings(
    val userId: String = "",
    val enabled: Boolean = true,
    val enabledTypes: List<NotificationType> = listOf(),
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 22, // Hour in 24-hour format (0-23)
    val quietHoursEnd: Int = 8, // Hour in 24-hour format (0-23)
    val emailNotificationsEnabled: Boolean = true,
    val pushNotificationsEnabled: Boolean = true,
    val inAppNotificationsEnabled: Boolean = true,
    val smsNotificationsEnabled: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val newMatchNotify: Boolean = true,
    val messageNotify: Boolean = true,
    val likeNotify: Boolean = true,
    val offerNotify: Boolean = true,
    val callNotify: Boolean = true,
    val verificationNotify: Boolean = true,
    val paymentNotify: Boolean = true,
    val subscriptionNotify: Boolean = true,
    val promotionNotify: Boolean = false,
    val profileVisitNotify: Boolean = true,
    val updatedAt: Date = Date()
) : Parcelable

/**
 * Device registration for push notifications
 */
@Parcelize
data class DeviceRegistration(
    val id: String = "",
    val userId: String = "",
    val token: String = "",
    val platform: DevicePlatform = DevicePlatform.ANDROID,
    val deviceModel: String = "",
    val appVersion: String = "",
    val osVersion: String = "",
    val language: String = "",
    val timezone: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastActive: Date = Date(),
    val isActive: Boolean = true
) : Parcelable

/**
 * Device platform enum
 */
@Parcelize
enum class DevicePlatform : Parcelable {
    ANDROID,
    IOS,
    WEB
}