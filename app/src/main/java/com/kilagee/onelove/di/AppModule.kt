package com.kilagee.onelove.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kilagee.onelove.data.database.OneLoveDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
    
    @Provides
    @Singleton
    fun provideOneLoveDatabase(@ApplicationContext context: Context): OneLoveDatabase {
        return Room.databaseBuilder(
            context,
            OneLoveDatabase::class.java,
            "onelove_database"
        ).fallbackToDestructiveMigration().build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: OneLoveDatabase) = database.userDao()
    
    @Provides
    @Singleton
    fun providePostDao(database: OneLoveDatabase) = database.postDao()
    
    @Provides
    @Singleton
    fun provideMessageDao(database: OneLoveDatabase) = database.messageDao()
    
    @Provides
    @Singleton
    fun provideChatDao(database: OneLoveDatabase) = database.chatDao()
    
    @Provides
    @Singleton
    fun provideOfferDao(database: OneLoveDatabase) = database.offerDao()
}