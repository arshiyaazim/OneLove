package com.kilagee.onelove.domain.model

import java.util.UUID

/**
 * Data class representing an AI profile in the app
 */
data class AIProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val photoUrl: String = "",
    val age: Int = 25,
    val gender: String = "",
    val country: String = "",
    val city: String = "",
    val personality: String = "",
    val bio: String = "",
    val interests: List<String> = emptyList(),
    val conversationId: String = UUID.randomUUID().toString(),
    val personalityTags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Creates a map representation of this AIProfile object
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "photoUrl" to photoUrl,
            "age" to age,
            "gender" to gender,
            "country" to country,
            "city" to city,
            "personality" to personality,
            "bio" to bio,
            "interests" to interests,
            "conversationId" to conversationId,
            "personalityTags" to personalityTags,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
    
    companion object {
        /**
         * Creates an AIProfile from a map
         */
        fun fromMap(map: Map<String, Any?>): AIProfile {
            return AIProfile(
                id = map["id"] as? String ?: UUID.randomUUID().toString(),
                name = map["name"] as? String ?: "",
                photoUrl = map["photoUrl"] as? String ?: "",
                age = (map["age"] as? Long)?.toInt() ?: 25,
                gender = map["gender"] as? String ?: "",
                country = map["country"] as? String ?: "",
                city = map["city"] as? String ?: "",
                personality = map["personality"] as? String ?: "",
                bio = map["bio"] as? String ?: "",
                interests = (map["interests"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                conversationId = map["conversationId"] as? String ?: UUID.randomUUID().toString(),
                personalityTags = (map["personalityTags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                createdAt = (map["createdAt"] as? Long) ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Long) ?: System.currentTimeMillis()
            )
        }
    }
}