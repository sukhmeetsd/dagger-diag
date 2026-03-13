package com.example.ecommerce.ui.product

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.domain.usecase.AddToCartUseCase
import com.example.ecommerce.domain.usecase.AddToFavoritesUseCase
import com.example.ecommerce.domain.usecase.GetProductDetailUseCase
import com.example.ecommerce.domain.usecase.SubmitReviewUseCase
import javax.inject.Inject

class ProductDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var productDetailViewModel: ProductDetailViewModel

    @Inject
    lateinit var getProductDetailUseCase: GetProductDetailUseCase

    @Inject
    lateinit var addToCartUseCase: AddToCartUseCase

    @Inject
    lateinit var addToFavoritesUseCase: AddToFavoritesUseCase

    @Inject
    lateinit var submitReviewUseCase: SubmitReviewUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DaggerAppComponent.create().inject(this)

        val productId = intent.getStringExtra("productId") ?: ""
        productDetailViewModel.loadProduct(productId)
    }
}
