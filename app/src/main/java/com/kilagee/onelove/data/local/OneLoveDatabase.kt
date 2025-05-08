package com.kilagee.onelove.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kilagee.onelove.data.local.dao.ChatDao
import com.kilagee.onelove.data.local.dao.MatchDao
import com.kilagee.onelove.data.local.dao.MessageDao
import com.kilagee.onelove.data.local.dao.NotificationDao
import com.kilagee.onelove.data.local.dao.PaymentDao
import com.kilagee.onelove.data.local.dao.UserDao
import com.kilagee.onelove.data.model.Chat
import com.kilagee.onelove.data.model.Match
import com.kilagee.onelove.data.model.MatchRequest
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.data.model.Notification
import com.kilagee.onelove.data.model.PaymentMethod
import com.kilagee.onelove.data.model.SubscriptionPlan
import com.kilagee.onelove.data.model.SubscriptionStatus
import com.kilagee.onelove.data.model.Transaction
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.UserInteraction
import com.kilagee.onelove.data.model.VerificationRequest

/**
 * Main Room database for the app
 */
@Database(
    entities = [
        User::class,
        Chat::class,
        Message::class,
        Match::class,
        MatchRequest::class,
        Notification::class,
        UserInteraction::class,
        VerificationRequest::class,
        SubscriptionPlan::class,
        SubscriptionStatus::class,
        PaymentMethod::class,
        Transaction::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class OneLoveDatabase : RoomDatabase() {

    /**
     * DAO for user operations
     */
    abstract fun userDao(): UserDao
    
    /**
     * DAO for chat operations
     */
    abstract fun chatDao(): ChatDao
    
    /**
     * DAO for message operations
     */
    abstract fun messageDao(): MessageDao
    
    /**
     * DAO for match operations
     */
    abstract fun matchDao(): MatchDao
    
    /**
     * DAO for notification operations
     */
    abstract fun notificationDao(): NotificationDao
    
    /**
     * DAO for payment operations
     */
    abstract fun paymentDao(): PaymentDao
}