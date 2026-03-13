package com.example.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.app.data.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module that provides database-related dependencies
 */
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): RoomDatabase {
        return Room.databaseBuilder(
            context,
            RoomDatabase::class.java,
            "app-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: RoomDatabase): UserDao {
        // In real implementation, database would have a userDao() method
        return database as UserDao
    }
}
