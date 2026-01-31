package com.example.ecommerce.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.domain.usecase.GetProductsUseCase
import com.example.ecommerce.domain.usecase.LoginUseCase
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var getProductsUseCase: GetProductsUseCase

    @Inject
    lateinit var loginUseCase: LoginUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DaggerAppComponent.create().inject(this)

        // Use injected dependencies
        mainViewModel.loadProducts()
    }
}
