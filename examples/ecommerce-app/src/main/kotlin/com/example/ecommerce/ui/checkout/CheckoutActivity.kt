package com.example.ecommerce.ui.checkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.data.repository.AddressRepository
import com.example.ecommerce.domain.usecase.GetCartItemsUseCase
import com.example.ecommerce.domain.usecase.PlaceOrderUseCase
import com.example.ecommerce.domain.usecase.ProcessPaymentUseCase
import javax.inject.Inject

class CheckoutActivity : AppCompatActivity() {

    @Inject
    lateinit var checkoutViewModel: CheckoutViewModel

    @Inject
    lateinit var placeOrderUseCase: PlaceOrderUseCase

    @Inject
    lateinit var processPaymentUseCase: ProcessPaymentUseCase

    @Inject
    lateinit var getCartItemsUseCase: GetCartItemsUseCase

    @Inject
    lateinit var addressRepository: AddressRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DaggerAppComponent.create().inject(this)

        checkoutViewModel.loadCheckoutData()
    }
}
