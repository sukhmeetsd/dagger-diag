package com.example.ecommerce.di

import com.example.ecommerce.domain.usecase.*
import com.example.ecommerce.ui.cart.CartViewModel
import com.example.ecommerce.ui.checkout.CheckoutViewModel
import com.example.ecommerce.ui.main.MainViewModel
import com.example.ecommerce.ui.product.ProductDetailViewModel
import com.example.ecommerce.ui.profile.ProfileViewModel
import com.example.ecommerce.ui.search.SearchViewModel
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    fun provideMainViewModel(
        getProductsUseCase: GetProductsUseCase,
        getFavoritesUseCase: GetFavoritesUseCase
    ): MainViewModel {
        return MainViewModel(getProductsUseCase, getFavoritesUseCase)
    }

    @Provides
    fun provideProductDetailViewModel(
        getProductDetailUseCase: GetProductDetailUseCase,
        addToCartUseCase: AddToCartUseCase,
        addToFavoritesUseCase: AddToFavoritesUseCase
    ): ProductDetailViewModel {
        return ProductDetailViewModel(
            getProductDetailUseCase,
            addToCartUseCase,
            addToFavoritesUseCase
        )
    }

    @Provides
    fun provideCartViewModel(
        getCartItemsUseCase: GetCartItemsUseCase,
        removeFromCartUseCase: RemoveFromCartUseCase
    ): CartViewModel {
        return CartViewModel(getCartItemsUseCase, removeFromCartUseCase)
    }

    @Provides
    fun provideCheckoutViewModel(
        placeOrderUseCase: PlaceOrderUseCase,
        processPaymentUseCase: ProcessPaymentUseCase,
        getCartItemsUseCase: GetCartItemsUseCase
    ): CheckoutViewModel {
        return CheckoutViewModel(
            placeOrderUseCase,
            processPaymentUseCase,
            getCartItemsUseCase
        )
    }

    @Provides
    fun provideProfileViewModel(
        getUserProfileUseCase: GetUserProfileUseCase,
        updateProfileUseCase: UpdateProfileUseCase,
        getOrderHistoryUseCase: GetOrderHistoryUseCase,
        logoutUseCase: LogoutUseCase
    ): ProfileViewModel {
        return ProfileViewModel(
            getUserProfileUseCase,
            updateProfileUseCase,
            getOrderHistoryUseCase,
            logoutUseCase
        )
    }

    @Provides
    fun provideSearchViewModel(
        searchProductsUseCase: SearchProductsUseCase
    ): SearchViewModel {
        return SearchViewModel(searchProductsUseCase)
    }
}
