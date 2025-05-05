package com.kilagee.onelove.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kilagee.onelove.data.database.OneLoveDatabase
import com.kilagee.onelove.data.repository.FirebaseAuthRepository
import com.kilagee.onelove.data.repository.FirebaseChatRepository
import com.kilagee.onelove.domain.repository.AuthRepository
import com.kilagee.onelove.domain.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // Firebase
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    
    // Room Database
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): OneLoveDatabase {
        return Room.databaseBuilder(
            context,
            OneLoveDatabase::class.java,
            "onelove_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    // DAOs
    @Singleton
    @Provides
    fun provideUserDao(database: OneLoveDatabase) = database.userDao()
    
    @Singleton
    @Provides
    fun providePostDao(database: OneLoveDatabase) = database.postDao()
    
    @Singleton
    @Provides
    fun provideMessageDao(database: OneLoveDatabase) = database.messageDao()
    
    @Singleton
    @Provides
    fun provideChatDao(database: OneLoveDatabase) = database.chatDao()
    
    @Singleton
    @Provides
    fun provideOfferDao(database: OneLoveDatabase) = database.offerDao()
    
    // Repositories
    @Singleton
    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        userDao: com.kilagee.onelove.data.database.dao.UserDao
    ): AuthRepository {
        return FirebaseAuthRepository(auth, firestore, storage, userDao)
    }
    
    @Singleton
    @Provides
    fun provideChatRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        chatDao: com.kilagee.onelove.data.database.dao.ChatDao,
        messageDao: com.kilagee.onelove.data.database.dao.MessageDao
    ): ChatRepository {
        return FirebaseChatRepository(auth, firestore, chatDao, messageDao)
    }
}