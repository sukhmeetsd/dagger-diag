package com.example.ecommerce.di

import com.example.ecommerce.data.repository.*
import com.example.ecommerce.domain.usecase.*
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    fun provideGetProductsUseCase(
        productRepository: ProductRepository
    ): GetProductsUseCase {
        return GetProductsUseCase(productRepository)
    }

    @Provides
    fun provideGetProductDetailUseCase(
        productRepository: ProductRepository
    ): GetProductDetailUseCase {
        return GetProductDetailUseCase(productRepository)
    }

    @Provides
    fun provideSearchProductsUseCase(
        searchRepository: SearchRepository
    ): SearchProductsUseCase {
        return SearchProductsUseCase(searchRepository)
    }

    @Provides
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    fun provideLogoutUseCase(
        authRepository: AuthRepository
    ): LogoutUseCase {
        return LogoutUseCase(authRepository)
    }

    @Provides
    fun provideRegisterUseCase(
        authRepository: AuthRepository
    ): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    fun provideAddToCartUseCase(
        cartRepository: CartRepository
    ): AddToCartUseCase {
        return AddToCartUseCase(cartRepository)
    }

    @Provides
    fun provideRemoveFromCartUseCase(
        cartRepository: CartRepository
    ): RemoveFromCartUseCase {
        return RemoveFromCartUseCase(cartRepository)
    }

    @Provides
    fun provideGetCartItemsUseCase(
        cartRepository: CartRepository
    ): GetCartItemsUseCase {
        return GetCartItemsUseCase(cartRepository)
    }

    @Provides
    fun providePlaceOrderUseCase(
        orderRepository: OrderRepository,
        cartRepository: CartRepository
    ): PlaceOrderUseCase {
        return PlaceOrderUseCase(orderRepository, cartRepository)
    }

    @Provides
    fun provideGetOrderHistoryUseCase(
        orderRepository: OrderRepository
    ): GetOrderHistoryUseCase {
        return GetOrderHistoryUseCase(orderRepository)
    }

    @Provides
    fun provideProcessPaymentUseCase(
        paymentRepository: PaymentRepository
    ): ProcessPaymentUseCase {
        return ProcessPaymentUseCase(paymentRepository)
    }

    @Provides
    fun provideAddToFavoritesUseCase(
        favoriteRepository: FavoriteRepository
    ): AddToFavoritesUseCase {
        return AddToFavoritesUseCase(favoriteRepository)
    }

    @Provides
    fun provideGetFavoritesUseCase(
        favoriteRepository: FavoriteRepository
    ): GetFavoritesUseCase {
        return GetFavoritesUseCase(favoriteRepository)
    }

    @Provides
    fun provideSubmitReviewUseCase(
        reviewRepository: ReviewRepository
    ): SubmitReviewUseCase {
        return SubmitReviewUseCase(reviewRepository)
    }

    @Provides
    fun provideGetUserProfileUseCase(
        userRepository: UserRepository
    ): GetUserProfileUseCase {
        return GetUserProfileUseCase(userRepository)
    }

    @Provides
    fun provideUpdateProfileUseCase(
        userRepository: UserRepository
    ): UpdateProfileUseCase {
        return UpdateProfileUseCase(userRepository)
    }
}
