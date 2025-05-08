package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Notification
import com.kilagee.onelove.data.model.NotificationType
import com.kilagee.onelove.data.model.PushNotificationSettings
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Interface for notification-related operations
 */
interface NotificationRepository {
    
    /**
     * Get all notifications for the current user
     * @param limit Maximum number of notifications to fetch
     * @param offset Offset for pagination
     * @return List of [Notification] objects
     */
    suspend fun getNotifications(limit: Int = 20, offset: Int = 0): Result<List<Notification>>
    
    /**
     * Get notifications as a flow for real-time updates
     * @param limit Maximum number of notifications to fetch initially
     * @return Flow of [Notification] lists
     */
    fun getNotificationsFlow(limit: Int = 20): Flow<Result<List<Notification>>>
    
    /**
     * Get unread notifications count
     * @return Number of unread notifications
     */
    suspend fun getUnreadCount(): Result<Int>
    
    /**
     * Get unread count as a flow for real-time updates
     * @return Flow of unread count
     */
    fun getUnreadCountFlow(): Flow<Result<Int>>
    
    /**
     * Mark notifications as read
     * @param notificationIds List of notification IDs to mark as read
     */
    suspend fun markAsRead(notificationIds: List<String>): Result<Unit>
    
    /**
     * Mark all notifications as read
     */
    suspend fun markAllAsRead(): Result<Unit>
    
    /**
     * Delete a notification
     * @param notificationId ID of the notification to delete
     */
    suspend fun deleteNotification(notificationId: String): Result<Unit>
    
    /**
     * Delete all notifications
     */
    suspend fun deleteAllNotifications(): Result<Unit>
    
    /**
     * Get notification settings
     * @return The [PushNotificationSettings] for the current user
     */
    suspend fun getNotificationSettings(): Result<PushNotificationSettings>
    
    /**
     * Update notification settings
     * @param settings The updated [PushNotificationSettings]
     */
    suspend fun updateNotificationSettings(settings: PushNotificationSettings): Result<Unit>
    
    /**
     * Register device token for push notifications
     * @param token FCM token or APNS token
     * @param platform Platform type (ANDROID, IOS, WEB)
     */
    suspend fun registerDeviceToken(token: String, platform: String): Result<Unit>
    
    /**
     * Unregister device token for push notifications
     * @param token FCM token or APNS token to unregister
     */
    suspend fun unregisterDeviceToken(token: String): Result<Unit>
    
    /**
     * Send test notification
     * @param type Type of notification to test
     */
    suspend fun sendTestNotification(type: NotificationType): Result<Unit>
    
    /**
     * Get notification by ID
     * @param notificationId ID of the notification to retrieve
     * @return The [Notification] object
     */
    suspend fun getNotification(notificationId: String): Result<Notification>
    
    /**
     * Get notifications by type
     * @param type Type of notifications to retrieve
     * @param limit Maximum number of notifications to fetch
     * @return List of [Notification] objects
     */
    suspend fun getNotificationsByType(
        type: NotificationType,
        limit: Int = 20
    ): Result<List<Notification>>
    
    /**
     * Get notifications created after a specific date
     * @param date Date to filter notifications by
     * @param limit Maximum number of notifications to fetch
     * @return List of [Notification] objects
     */
    suspend fun getNotificationsAfter(
        date: Date,
        limit: Int = 20
    ): Result<List<Notification>>
}