package com.kilagee.onelove.domain.model

import java.util.UUID

/**
 * Data class representing an AI profile in the system
 */
data class AIProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    val personalityType: String = "",
    val interests: List<String> = emptyList(),
    val responseTemplates: Map<String, List<String>> = emptyMap(),
    val responses: List<AIResponse> = emptyList(),
    val isPremium: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String? = null,
    val updatedBy: String? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "bio" to bio,
            "photoUrl" to photoUrl,
            "personalityType" to personalityType,
            "interests" to interests,
            "responseTemplates" to responseTemplates,
            "responses" to responses.map { it.toMap() },
            "isPremium" to isPremium,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "createdBy" to createdBy,
            "updatedBy" to updatedBy
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): AIProfile {
            @Suppress("UNCHECKED_CAST")
            val responsesList = (map["responses"] as? List<Map<String, Any?>>)?.map {
                AIResponse.fromMap(it)
            } ?: emptyList()

            @Suppress("UNCHECKED_CAST")
            return AIProfile(
                id = map["id"] as? String ?: UUID.randomUUID().toString(),
                name = map["name"] as? String ?: "",
                bio = map["bio"] as? String ?: "",
                photoUrl = map["photoUrl"] as? String ?: "",
                personalityType = map["personalityType"] as? String ?: "",
                interests = (map["interests"] as? List<String>) ?: emptyList(),
                responseTemplates = (map["responseTemplates"] as? Map<String, List<String>>) ?: emptyMap(),
                responses = responsesList,
                isPremium = map["isPremium"] as? Boolean ?: false,
                isActive = map["isActive"] as? Boolean ?: true,
                createdAt = (map["createdAt"] as? Long) ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Long) ?: System.currentTimeMillis(),
                createdBy = map["createdBy"] as? String,
                updatedBy = map["updatedBy"] as? String
            )
        }
    }
}

/**
 * Data class representing an AI response template
 */
data class AIResponse(
    val id: String = UUID.randomUUID().toString(),
    val type: String = "", // flirty, romantic, serious, funny, etc.
    val text: String = "",
    val triggers: List<String> = emptyList(), // Keywords that trigger this response
    val priority: Int = 0 // Higher priority responses are selected first when multiple triggers match
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "type" to type,
            "text" to text,
            "triggers" to triggers,
            "priority" to priority
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): AIResponse {
            @Suppress("UNCHECKED_CAST")
            return AIResponse(
                id = map["id"] as? String ?: UUID.randomUUID().toString(),
                type = map["type"] as? String ?: "",
                text = map["text"] as? String ?: "",
                triggers = (map["triggers"] as? List<String>) ?: emptyList(),
                priority = (map["priority"] as? Int) ?: 0
            )
        }
    }
}