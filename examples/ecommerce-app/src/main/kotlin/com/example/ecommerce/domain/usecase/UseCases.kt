package com.example.ecommerce.domain.usecase

import com.example.ecommerce.data.repository.*

// Product use cases
class GetProductsUseCase(private val productRepository: ProductRepository)
class GetProductDetailUseCase(private val productRepository: ProductRepository)
class SearchProductsUseCase(private val searchRepository: SearchRepository)

// Auth use cases
class LoginUseCase(private val authRepository: AuthRepository)
class LogoutUseCase(private val authRepository: AuthRepository)
class RegisterUseCase(private val authRepository: AuthRepository)

// Cart use cases
class AddToCartUseCase(private val cartRepository: CartRepository)
class RemoveFromCartUseCase(private val cartRepository: CartRepository)
class GetCartItemsUseCase(private val cartRepository: CartRepository)

// Order use cases
class PlaceOrderUseCase(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
)
class GetOrderHistoryUseCase(private val orderRepository: OrderRepository)

// Payment use cases
class ProcessPaymentUseCase(private val paymentRepository: PaymentRepository)

// Favorite use cases
class AddToFavoritesUseCase(private val favoriteRepository: FavoriteRepository)
class GetFavoritesUseCase(private val favoriteRepository: FavoriteRepository)

// Review use cases
class SubmitReviewUseCase(private val reviewRepository: ReviewRepository)

// User use cases
class GetUserProfileUseCase(private val userRepository: UserRepository)
class UpdateProfileUseCase(private val userRepository: UserRepository)
