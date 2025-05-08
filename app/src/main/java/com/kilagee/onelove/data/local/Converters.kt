package com.kilagee.onelove.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Type converters for Room database
 */
class Converters {
    
    private val gson = Gson()
    
    /**
     * Convert Date to Long
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    
    /**
     * Convert Long to Date
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
    
    /**
     * Convert List<String> to String
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }
    
    /**
     * Convert String to List<String>
     */
    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
    
    /**
     * Convert Map<String, Any> to String
     */
    @TypeConverter
    fun fromMap(map: Map<String, Any>?): String? {
        return gson.toJson(map)
    }
    
    /**
     * Convert String to Map<String, Any>
     */
    @TypeConverter
    fun toMap(json: String?): Map<String, Any>? {
        if (json == null) return null
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(json, type)
    }
}