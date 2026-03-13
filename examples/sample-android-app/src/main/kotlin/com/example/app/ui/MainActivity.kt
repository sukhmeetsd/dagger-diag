package com.example.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app.data.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main activity that uses dependency injection
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject dependencies
        // DaggerAppComponent.create().inject(this)

        // Use the injected repository
        lifecycleScope.launch {
            repository.fetchAndStoreUsers()
            val users = repository.getUsers()
            // Update UI with users
        }
    }
}
