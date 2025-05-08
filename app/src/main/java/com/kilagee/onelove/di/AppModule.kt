package com.kilagee.onelove.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kilagee.onelove.data.local.OneLoveDatabase
import com.kilagee.onelove.data.repository.DefaultAuthRepository
import com.kilagee.onelove.data.repository.DefaultChatRepository
import com.kilagee.onelove.data.repository.DefaultMatchRepository
import com.kilagee.onelove.data.repository.DefaultNotificationRepository
import com.kilagee.onelove.data.repository.DefaultPaymentRepository
import com.kilagee.onelove.data.repository.DefaultUserRepository
import com.kilagee.onelove.domain.repository.AuthRepository
import com.kilagee.onelove.domain.repository.ChatRepository
import com.kilagee.onelove.domain.repository.MatchRepository
import com.kilagee.onelove.domain.repository.NotificationRepository
import com.kilagee.onelove.domain.repository.PaymentRepository
import com.kilagee.onelove.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt dependency injection module for providing app-wide singleton instances
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the Room database instance
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OneLoveDatabase {
        return Room.databaseBuilder(
            context,
            OneLoveDatabase::class.java,
            "onelove_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    /**
     * Provides the Firebase Auth instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
    
    /**
     * Provides the Firestore database instance
     */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
    
    /**
     * Provides the Firebase Storage instance
     */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
    
    /**
     * Provides Firebase Functions instance
     */
    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = Firebase.functions
    
    /**
     * Provides Firebase Cloud Messaging instance
     */
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
    
    /**
     * Provides Gson instance for JSON parsing
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()
        
    /**
     * Provides base Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.onelove.kilagee.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    /**
     * Provides IO dispatcher for background operations
     */
    @Provides
    @Named("IoDispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    
    /**
     * Provides Default dispatcher for general operations
     */
    @Provides
    @Named("DefaultDispatcher")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    
    /**
     * Provides User Repository implementation
     */
    @Provides
    @Singleton
    fun provideUserRepository(
        database: OneLoveDatabase,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        auth: FirebaseAuth,
        @Named("IoDispatcher") ioDispatcher: CoroutineDispatcher
    ): UserRepository = DefaultUserRepository(
        database.userDao(),
        firestore,
        storage,
        auth,
        ioDispatcher
    )
    
    /**
     * Provides Authentication Repository implementation
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        @Named("IoDispatcher") ioDispatcher: CoroutineDispatcher
    ): AuthRepository = DefaultAuthRepository(
        auth,
        firestore,
        ioDispatcher
    )
    
    /**
     * Provides Chat Repository implementation
     */
    @Provides
    @Singleton
    fun provideChatRepository(
        database: OneLoveDatabase,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        @Named("IoDispatcher") ioDispatcher: CoroutineDispatcher
    ): ChatRepository = DefaultChatRepository(
        database.chatDao(),
        firestore,
        auth,
        ioDispatcher
    )
    
    /**
     * Provides Match Repository implementation
     */
    @Provides
    @Singleton
    fun provideMatchRepository(
        database: OneLoveDatabase,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        @Named("IoDispatcher") ioDispatcher: CoroutineDispatcher
    ): MatchRepository = DefaultMatchRepository(
        database.matchDao(),
        firestore,
        auth,
        ioDispatcher
    )
    
    /**
     * Provides Notification Repository implementation
     */
    @Provides
    @Singleton
    fun provideNotificationRepository(
        database: OneLoveDatabase,
        firestore: FirebaseFirestore,
        messaging: FirebaseMessaging,
        auth: FirebaseAuth,
        @Named("IoDispatcher") ioDispatcher: CoroutineDispatcher
    ): NotificationRepository = DefaultNotificationRepository(
        database.notificationDao(),
        firestore,
        messaging,
        auth,
        ioDispatcher
    )
    
    /**
     * Provides Payment Repository implementation
     */
    @Provides
    @Singleton
    fun providePaymentRepository(
        database: OneLoveDatabase,
        firestore: FirebaseFirestore,
        functions: FirebaseFunctions,
        auth: FirebaseAuth,
        @Named("IoDispatcher") ioDispatcher: CoroutineDispatcher
    ): PaymentRepository = DefaultPaymentRepository(
        database.paymentDao(),
        firestore,
        functions,
        auth,
        ioDispatcher
    )
}