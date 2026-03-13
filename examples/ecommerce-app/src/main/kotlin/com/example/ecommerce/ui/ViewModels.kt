package com.example.ecommerce.ui.main

import com.example.ecommerce.domain.usecase.*

// ViewModels for UI layer
class MainViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase
) {
    fun loadProducts() { /* implementation */ }
}

package com.example.ecommerce.ui.product

import com.example.ecommerce.domain.usecase.*

class ProductDetailViewModel(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase
) {
    fun loadProduct(productId: String) { /* implementation */ }
}

package com.example.ecommerce.ui.cart

import com.example.ecommerce.domain.usecase.*

class CartViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) {
    fun loadCartItems() { /* implementation */ }
}

package com.example.ecommerce.ui.checkout

import com.example.ecommerce.domain.usecase.*

class CheckoutViewModel(
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase
) {
    fun loadCheckoutData() { /* implementation */ }
}

package com.example.ecommerce.ui.profile

import com.example.ecommerce.domain.usecase.*

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    private val logoutUseCase: LogoutUseCase
) {
    fun loadProfile() { /* implementation */ }
}

package com.example.ecommerce.ui.search

import com.example.ecommerce.domain.usecase.*

class SearchViewModel(
    private val searchProductsUseCase: SearchProductsUseCase
) {
    fun initialize() { /* implementation */ }
}
