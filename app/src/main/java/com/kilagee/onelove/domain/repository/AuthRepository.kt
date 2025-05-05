package com.kilagee.onelove.domain.repository

import android.net.Uri
import com.kilagee.onelove.domain.model.Resource
import com.kilagee.onelove.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Authentication methods
    fun login(email: String, password: String): Flow<Resource<User>>
    
    fun register(email: String, password: String, user: User, profileImageUri: Uri?, idDocumentUri: Uri?): Flow<Resource<User>>
    
    fun resetPassword(email: String): Flow<Resource<Unit>>
    
    fun logout(): Flow<Resource<Unit>>
    
    fun getCurrentUser(): Flow<Resource<User?>>
    
    fun updateUserProfile(user: User, profileImageUri: Uri? = null): Flow<Resource<User>>
    
    fun deleteAccount(): Flow<Resource<Unit>>
    
    fun isUserAuthenticated(): Boolean
}