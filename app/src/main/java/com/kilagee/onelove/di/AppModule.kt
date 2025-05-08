package com.kilagee.onelove.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kilagee.onelove.data.local.OneLoveDatabase
import com.kilagee.onelove.data.local.UserDao
import com.kilagee.onelove.data.repository.AuthRepositoryImpl
import com.kilagee.onelove.data.repository.DiscoverRepositoryImpl
import com.kilagee.onelove.data.repository.UserRepositoryImpl
import com.kilagee.onelove.domain.repository.AuthRepository
import com.kilagee.onelove.domain.repository.DiscoverRepository
import com.kilagee.onelove.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provide Firebase Auth
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }
    
    /**
     * Provide Firebase Firestore
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
    
    /**
     * Provide Firebase Storage
     */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
    
    /**
     * Provide Room database
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OneLoveDatabase {
        return OneLoveDatabase.getInstance(context)
    }
    
    /**
     * Provide User DAO
     */
    @Provides
    @Singleton
    fun provideUserDao(database: OneLoveDatabase): UserDao {
        return database.userDao()
    }
    
    /**
     * Provide Auth repository
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore, userDao)
    }
    
    /**
     * Provide User repository
     */
    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        userDao: UserDao
    ): UserRepository {
        return UserRepositoryImpl(firestore, userDao)
    }
    
    /**
     * Provide Discover repository
     */
    @Provides
    @Singleton
    fun provideDiscoverRepository(
        firestore: FirebaseFirestore,
        authRepository: AuthRepository,
        userDao: UserDao
    ): DiscoverRepository {
        return DiscoverRepositoryImpl(firestore, authRepository, userDao)
    }
}