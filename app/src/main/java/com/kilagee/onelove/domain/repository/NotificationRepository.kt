package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Notification
import com.kilagee.onelove.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for notification operations
 */
interface NotificationRepository {
    /**
     * Get all notifications for the current user
     * @param limit Maximum number of notifications to return
     * @param offset Pagination offset
     * @return Flow of Result containing a list of notifications or an error
     */
    fun getNotifications(limit: Int = 20, offset: Int = 0): Flow<Result<List<Notification>>>
    
    /**
     * Get unread notifications for the current user
     * @param limit Maximum number of notifications to return
     * @return Flow of Result containing a list of notifications or an error
     */
    fun getUnreadNotifications(limit: Int = 20): Flow<Result<List<Notification>>>
    
    /**
     * Mark a notification as read
     * @param notificationId ID of the notification to mark as read
     * @return Result indicating success or failure
     */
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit>
    
    /**
     * Mark all notifications as read
     * @return Result indicating success or failure
     */
    suspend fun markAllNotificationsAsRead(): Result<Unit>
    
    /**
     * Delete a notification
     * @param notificationId ID of the notification to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteNotification(notificationId: String): Result<Unit>
    
    /**
     * Delete all notifications
     * @return Result indicating success or failure
     */
    suspend fun deleteAllNotifications(): Result<Unit>
    
    /**
     * Get the count of unread notifications
     * @return Flow of Result containing the unread count or an error
     */
    fun getUnreadNotificationCount(): Flow<Result<Int>>
    
    /**
     * Register the device for push notifications
     * @return Result indicating success or failure
     */
    suspend fun registerDevice(): Result<Unit>
    
    /**
     * Unregister the device from push notifications
     * @return Result indicating success or failure
     */
    suspend fun unregisterDevice(): Result<Unit>
    
    /**
     * Update notification preferences
     * @param preferences Map of notification types to boolean preferences
     * @return Result indicating success or failure
     */
    suspend fun updateNotificationPreferences(preferences: Map<String, Boolean>): Result<Unit>
    
    /**
     * Get notification preferences
     * @return Flow of Result containing a map of notification types to boolean preferences
     */
    fun getNotificationPreferences(): Flow<Result<Map<String, Boolean>>>
    
    /**
     * Schedule a local notification
     * @param notification Notification to schedule
     * @param delayMillis Delay in milliseconds
     * @return Result containing the notification ID or an error
     */
    suspend fun scheduleLocalNotification(notification: Notification, delayMillis: Long): Result<String>
    
    /**
     * Cancel a scheduled local notification
     * @param notificationId ID of the notification to cancel
     * @return Result indicating success or failure
     */
    suspend fun cancelScheduledNotification(notificationId: String): Result<Unit>
}