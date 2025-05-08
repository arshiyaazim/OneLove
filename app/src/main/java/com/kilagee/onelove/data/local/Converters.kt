package com.kilagee.onelove.data.local

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kilagee.onelove.data.model.InteractionType
import com.kilagee.onelove.data.model.MatchRequestStatus
import com.kilagee.onelove.data.model.MatchStatus
import com.kilagee.onelove.data.model.MessageStatus
import com.kilagee.onelove.data.model.MessageType
import com.kilagee.onelove.data.model.NotificationType
import com.kilagee.onelove.data.model.PaymentMethodType
import com.kilagee.onelove.data.model.RelationshipPreference
import com.kilagee.onelove.data.model.SubscriptionTier
import com.kilagee.onelove.data.model.TransactionStatus
import com.kilagee.onelove.data.model.TransactionType
import com.kilagee.onelove.data.model.UserStatus
import com.kilagee.onelove.data.model.VerificationLevel
import com.kilagee.onelove.data.model.VerificationRequestStatus
import com.kilagee.onelove.data.model.VerificationType
import java.util.Date

/**
 * Type converters for Room database
 */
class Converters {
    private val gson = Gson()
    
    // List converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Map converters
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringMap(value: String?): Map<String, String> {
        if (value == null) return emptyMap()
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return try {
            gson.fromJson(value, mapType)
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    @TypeConverter
    fun fromIntMap(value: Map<String, Int>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toIntMap(value: String?): Map<String, Int> {
        if (value == null) return emptyMap()
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return try {
            gson.fromJson(value, mapType)
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    @TypeConverter
    fun fromBooleanMap(value: Map<String, Boolean>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toBooleanMap(value: String?): Map<String, Boolean> {
        if (value == null) return emptyMap()
        val mapType = object : TypeToken<Map<String, Boolean>>() {}.type
        return try {
            gson.fromJson(value, mapType)
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    // Timestamp converters
    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? {
        return value?.seconds
    }
    
    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it, 0) }
    }
    
    // Date converters
    @TypeConverter
    fun fromDate(value: Date?): Long? {
        return value?.time
    }
    
    @TypeConverter
    fun toDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    // Enum converters
    @TypeConverter
    fun fromVerificationLevel(value: VerificationLevel?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toVerificationLevel(value: String?): VerificationLevel? {
        return value?.let { enumValueOf<VerificationLevel>(it) }
    }
    
    @TypeConverter
    fun fromRelationshipPreference(value: RelationshipPreference?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toRelationshipPreference(value: String?): RelationshipPreference? {
        return value?.let { enumValueOf<RelationshipPreference>(it) }
    }
    
    @TypeConverter
    fun fromRelationshipPreferenceList(value: List<RelationshipPreference>?): String? {
        return value?.let { list -> gson.toJson(list.map { it.name }) }
    }
    
    @TypeConverter
    fun toRelationshipPreferenceList(value: String?): List<RelationshipPreference> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            val stringList: List<String> = gson.fromJson(value, listType)
            stringList.mapNotNull { name ->
                try {
                    enumValueOf<RelationshipPreference>(name)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    @TypeConverter
    fun fromSubscriptionTier(value: SubscriptionTier?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toSubscriptionTier(value: String?): SubscriptionTier? {
        return value?.let { enumValueOf<SubscriptionTier>(it) }
    }
    
    @TypeConverter
    fun fromUserStatus(value: UserStatus?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toUserStatus(value: String?): UserStatus? {
        return value?.let { enumValueOf<UserStatus>(it) }
    }
    
    @TypeConverter
    fun fromNotificationType(value: NotificationType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toNotificationType(value: String?): NotificationType? {
        return value?.let { enumValueOf<NotificationType>(it) }
    }
    
    @TypeConverter
    fun fromInteractionType(value: InteractionType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toInteractionType(value: String?): InteractionType? {
        return value?.let { enumValueOf<InteractionType>(it) }
    }
    
    @TypeConverter
    fun fromMessageType(value: MessageType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toMessageType(value: String?): MessageType? {
        return value?.let { enumValueOf<MessageType>(it) }
    }
    
    @TypeConverter
    fun fromMessageStatus(value: MessageStatus?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toMessageStatus(value: String?): MessageStatus? {
        return value?.let { enumValueOf<MessageStatus>(it) }
    }
    
    @TypeConverter
    fun fromMatchStatus(value: MatchStatus?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toMatchStatus(value: String?): MatchStatus? {
        return value?.let { enumValueOf<MatchStatus>(it) }
    }
    
    @TypeConverter
    fun fromMatchRequestStatus(value: MatchRequestStatus?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toMatchRequestStatus(value: String?): MatchRequestStatus? {
        return value?.let { enumValueOf<MatchRequestStatus>(it) }
    }
    
    @TypeConverter
    fun fromVerificationType(value: VerificationType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toVerificationType(value: String?): VerificationType? {
        return value?.let { enumValueOf<VerificationType>(it) }
    }
    
    @TypeConverter
    fun fromVerificationRequestStatus(value: VerificationRequestStatus?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toVerificationRequestStatus(value: String?): VerificationRequestStatus? {
        return value?.let { enumValueOf<VerificationRequestStatus>(it) }
    }
    
    @TypeConverter
    fun fromPaymentMethodType(value: PaymentMethodType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toPaymentMethodType(value: String?): PaymentMethodType? {
        return value?.let { enumValueOf<PaymentMethodType>(it) }
    }
    
    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? {
        return value?.let { enumValueOf<TransactionType>(it) }
    }
    
    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toTransactionStatus(value: String?): TransactionStatus? {
        return value?.let { enumValueOf<TransactionStatus>(it) }
    }
}