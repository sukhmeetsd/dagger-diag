package com.example.ecommerce.ui.cart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.domain.usecase.GetCartItemsUseCase
import com.example.ecommerce.domain.usecase.RemoveFromCartUseCase
import javax.inject.Inject

class CartActivity : AppCompatActivity() {

    @Inject
    lateinit var cartViewModel: CartViewModel

    @Inject
    lateinit var getCartItemsUseCase: GetCartItemsUseCase

    @Inject
    lateinit var removeFromCartUseCase: RemoveFromCartUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DaggerAppComponent.create().inject(this)

        cartViewModel.loadCartItems()
    }
}
