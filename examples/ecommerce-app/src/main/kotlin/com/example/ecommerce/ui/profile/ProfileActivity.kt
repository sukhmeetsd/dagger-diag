package com.example.ecommerce.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.data.repository.UserRepository
import com.example.ecommerce.domain.usecase.GetOrderHistoryUseCase
import com.example.ecommerce.domain.usecase.GetUserProfileUseCase
import com.example.ecommerce.domain.usecase.LogoutUseCase
import com.example.ecommerce.domain.usecase.UpdateProfileUseCase
import javax.inject.Inject

class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var getUserProfileUseCase: GetUserProfileUseCase

    @Inject
    lateinit var updateProfileUseCase: UpdateProfileUseCase

    @Inject
    lateinit var getOrderHistoryUseCase: GetOrderHistoryUseCase

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DaggerAppComponent.create().inject(this)

        profileViewModel.loadProfile()
    }
}
