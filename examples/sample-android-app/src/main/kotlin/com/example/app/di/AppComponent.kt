package com.example.app.di

import com.example.app.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Main application component that provides dependencies to the app
 */
@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DatabaseModule::class,
        AppModule::class
    ]
)
interface AppComponent {
    fun inject(activity: MainActivity)
}
