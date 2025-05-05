package com.kilagee.onelove.domain.repository

import com.kilagee.onelove.data.model.Notification
import com.kilagee.onelove.domain.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for notification operations
 */
interface NotificationRepository {
    
    /**
     * Get all notifications for a user
     */
    fun getUserNotifications(userId: String): Flow<List<Notification>>
    
    /**
     * Get unread notifications for a user
     */
    fun getUnreadNotifications(userId: String): Flow<List<Notification>>
    
    /**
     * Get notification by ID
     */
    fun getNotificationById(notificationId: String): Flow<Resource<Notification>>
    
    /**
     * Mark a notification as read
     */
    fun markNotificationAsRead(notificationId: String): Flow<Resource<Unit>>
    
    /**
     * Mark all notifications as read for a user
     */
    fun markAllNotificationsAsRead(userId: String): Flow<Resource<Unit>>
    
    /**
     * Create a new notification
     */
    fun createNotification(notification: Notification): Flow<Resource<Notification>>
    
    /**
     * Delete a notification
     */
    fun deleteNotification(notificationId: String): Flow<Resource<Unit>>
    
    /**
     * Delete all notifications for a user
     */
    fun deleteAllNotifications(userId: String): Flow<Resource<Unit>>
    
    /**
     * Get unread notification count for a user
     */
    fun getUnreadNotificationCount(userId: String): Flow<Int>
}