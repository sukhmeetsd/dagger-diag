package com.example.app.di

import com.example.app.data.ApiService
import com.example.app.data.Repository
import com.example.app.data.RepositoryImpl
import com.example.app.data.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module that provides application-level dependencies
 */
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideRepository(
        apiService: ApiService,
        userDao: UserDao
    ): Repository {
        return RepositoryImpl(apiService, userDao)
    }
}
