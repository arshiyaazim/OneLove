package com.kilagee.onelove.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "first_name")
    val firstName: String,
    
    @ColumnInfo(name = "last_name")
    val lastName: String,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Date,
    
    @ColumnInfo(name = "gender")
    val gender: String,
    
    @ColumnInfo(name = "bio")
    val bio: String = "",
    
    @ColumnInfo(name = "country")
    val country: String,
    
    @ColumnInfo(name = "city")
    val city: String,
    
    @ColumnInfo(name = "profile_picture_url")
    val profilePictureUrl: String = "",
    
    @ColumnInfo(name = "verification_status")
    val verificationStatus: VerificationStatus = VerificationStatus.NOT_VERIFIED,
    
    @ColumnInfo(name = "id_document_url")
    val idDocumentUrl: String = "",
    
    @ColumnInfo(name = "is_online")
    val isOnline: Boolean = false,
    
    @ColumnInfo(name = "last_active")
    val lastActive: Date = Date(),
    
    @ColumnInfo(name = "points")
    val points: Int = 0,
    
    @ColumnInfo(name = "wallet_balance")
    val walletBalance: Double = 0.0,
    
    @ColumnInfo(name = "membership_level")
    val membershipLevel: MembershipLevel = MembershipLevel.BASIC,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "interests")
    val interests: List<String> = emptyList()
)

enum class VerificationStatus {
    NOT_VERIFIED,
    PENDING,
    TEMPORARILY_APPROVED,
    FULLY_VERIFIED,
    REJECTED
}

enum class MembershipLevel {
    BASIC,
    PREMIUM,
    VIP
}