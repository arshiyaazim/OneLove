package com.kilagee.onelove.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.kilagee.onelove.data.repository.AdminRepositoryImpl
import com.kilagee.onelove.domain.repository.AdminRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAdminRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        messaging: FirebaseMessaging
    ): AdminRepository {
        return AdminRepositoryImpl(firestore, auth, messaging)
    }
}