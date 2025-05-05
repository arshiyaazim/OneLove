package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Notification
import com.kilagee.onelove.data.model.NotificationActionType
import com.kilagee.onelove.data.model.NotificationType
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for notification-related operations
 */
interface NotificationRepository {
    
    /**
     * Create a new notification
     */
    fun createNotification(
        title: String,
        message: String,
        type: NotificationType,
        relatedId: String? = null,
        imageUrl: String? = null,
        actionType: NotificationActionType = NotificationActionType.NONE,
        actionData: String? = null
    ): Flow<Resource<Notification>>
    
    /**
     * Get all notifications for current user
     */
    fun getNotifications(): Flow<Resource<List<Notification>>>
    
    /**
     * Get notifications by type for current user
     */
    fun getNotificationsByType(type: NotificationType): Flow<Resource<List<Notification>>>
    
    /**
     * Get unread notifications count
     */
    fun getUnreadNotificationsCount(): Flow<Resource<Int>>
    
    /**
     * Mark a notification as read
     */
    fun markNotificationAsRead(notificationId: String): Flow<Resource<Notification>>
    
    /**
     * Mark all notifications as read
     */
    fun markAllNotificationsAsRead(): Flow<Resource<Unit>>
    
    /**
     * Delete a notification
     */
    fun deleteNotification(notificationId: String): Flow<Resource<Unit>>
    
    /**
     * Delete all notifications
     */
    fun deleteAllNotifications(): Flow<Resource<Unit>>
    
    /**
     * Delete notifications by type
     */
    fun deleteNotificationsByType(type: NotificationType): Flow<Resource<Unit>>
    
    /**
     * Update FCM token
     */
    fun updateFcmToken(token: String): Flow<Resource<Unit>>
    
    /**
     * Enable or disable notifications
     */
    fun setNotificationsEnabled(enabled: Boolean): Flow<Resource<Unit>>
    
    /**
     * Check if notifications are enabled
     */
    fun areNotificationsEnabled(): Flow<Resource<Boolean>>
}