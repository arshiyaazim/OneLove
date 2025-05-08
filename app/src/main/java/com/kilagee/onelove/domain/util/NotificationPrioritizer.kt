package com.kilagee.onelove.domain.util

import com.kilagee.onelove.data.model.Notification
import com.kilagee.onelove.data.model.NotificationType
import com.kilagee.onelove.data.model.UserInteraction
import com.kilagee.onelove.data.model.UserPreferences
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Smart notification prioritization system that uses contextual data
 * to determine notification importance and delivery methods.
 * 
 * This class implements algorithms that adapt to user behavior patterns,
 * interaction history, and time-of-day to provide a more personalized
 * notification experience.
 */
@Singleton
class NotificationPrioritizer @Inject constructor() {

    /**
     * Prioritize a list of notifications based on various factors
     * 
     * @param notifications List of notifications to prioritize
     * @param userPreferences User preferences for notifications
     * @param userInteractions Recent user interactions with the app
     * @param currentTime Current device time in milliseconds
     * @return Sorted list of notifications by priority
     */
    fun prioritizeNotifications(
        notifications: List<Notification>,
        userPreferences: UserPreferences,
        userInteractions: List<UserInteraction>,
        currentTime: Long
    ): List<Notification> {
        if (notifications.isEmpty()) return emptyList()
        
        Timber.d("Prioritizing ${notifications.size} notifications")
        
        return notifications.sortedByDescending { notification ->
            calculateNotificationScore(
                notification,
                userPreferences,
                userInteractions,
                currentTime
            )
        }
    }
    
    /**
     * Calculate a priority score for a single notification
     * 
     * Higher scores indicate higher priority notifications
     * 
     * @param notification The notification to score
     * @param userPreferences User preferences for notifications
     * @param userInteractions Recent user interactions with the app
     * @param currentTime Current device time in milliseconds
     * @return Priority score (higher = more important)
     */
    private fun calculateNotificationScore(
        notification: Notification,
        userPreferences: UserPreferences,
        userInteractions: List<UserInteraction>,
        currentTime: Long
    ): Double {
        var score = 0.0
        
        // Base score by notification type
        score += when (notification.type) {
            NotificationType.NEW_MATCH -> 100.0
            NotificationType.MESSAGE -> 90.0
            NotificationType.INCOMING_CALL -> 120.0
            NotificationType.MISSED_CALL -> 80.0
            NotificationType.LIKE -> 60.0
            NotificationType.PROFILE_VIEW -> 40.0
            NotificationType.SUBSCRIPTION -> 70.0
            NotificationType.VERIFICATION -> 85.0
            NotificationType.SYSTEM -> 30.0
            NotificationType.PROMOTIONAL -> 10.0
        }
        
        // Adjust score based on user preferences
        if (userPreferences.priorityContacts.contains(notification.senderId)) {
            score *= 1.5 // 50% boost for priority contacts
        }
        
        if (userPreferences.mutedContacts.contains(notification.senderId)) {
            score *= 0.1 // 90% reduction for muted contacts
        }
        
        // Check for recent interactions with this sender
        val recentInteractions = userInteractions.filter { 
            it.userId == notification.senderId && 
            (currentTime - it.timestamp) < RECENT_INTERACTION_THRESHOLD 
        }
        
        if (recentInteractions.isNotEmpty()) {
            // Boost score if user has recently interacted with this sender
            score *= 1.3
        }
        
        // Time-based adjustments
        val recencyBoost = calculateRecencyBoost(notification.timestamp, currentTime)
        score *= recencyBoost
        
        // Apply time-of-day weighting (messages at 3 AM get lower priority unless user is active)
        val isActiveNow = userInteractions.any { 
            (currentTime - it.timestamp) < ACTIVE_USER_THRESHOLD 
        }
        
        if (!isActiveNow) {
            val timeOfDayFactor = calculateTimeOfDayFactor(currentTime)
            score *= timeOfDayFactor
        }
        
        Timber.d("Notification score for ${notification.id}: $score")
        return score
    }
    
    /**
     * Calculate a boost factor based on how recent the notification is
     * 
     * @param notificationTime Timestamp of the notification
     * @param currentTime Current device time
     * @return Recency factor (1.0 for brand new, decreasing with age)
     */
    private fun calculateRecencyBoost(notificationTime: Long, currentTime: Long): Double {
        val ageInMinutes = (currentTime - notificationTime) / (1000 * 60)
        
        // Exponential decay: notifications lose priority as they age
        return when {
            ageInMinutes < 5 -> 1.0
            ageInMinutes < 30 -> 0.9
            ageInMinutes < 60 -> 0.8
            ageInMinutes < 180 -> 0.7
            ageInMinutes < 360 -> 0.6
            ageInMinutes < 720 -> 0.5
            else -> 0.4
        }
    }
    
    /**
     * Calculate a time-of-day factor to reduce notification priority during sleeping hours
     * 
     * @param currentTime Current device time
     * @return Time of day factor (lower during typical sleeping hours)
     */
    private fun calculateTimeOfDayFactor(currentTime: Long): Double {
        // Extract hour of day (0-23)
        val hour = java.time.Instant.ofEpochMilli(currentTime)
            .atZone(java.time.ZoneId.systemDefault())
            .hour
            
        // Apply reduced priority during typical sleeping hours
        return when {
            hour in 0..5 -> 0.5  // Late night/early morning
            hour in 6..7 -> 0.7  // Early morning
            hour in 8..22 -> 1.0 // Normal waking hours
            hour in 23..24 -> 0.7 // Late evening
            else -> 1.0
        }
    }
    
    companion object {
        // Time thresholds in milliseconds
        private const val RECENT_INTERACTION_THRESHOLD = 3 * 60 * 60 * 1000L // 3 hours
        private const val ACTIVE_USER_THRESHOLD = 10 * 60 * 1000L // 10 minutes
    }
}