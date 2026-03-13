package com.example.ecommerce.di

import com.example.ecommerce.data.local.*
import com.example.ecommerce.data.remote.*
import com.example.ecommerce.data.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        productApi: ProductApi,
        productDao: ProductDao
    ): ProductRepository {
        return ProductRepositoryImpl(productApi, productDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userApi: UserApi,
        userDao: UserDao
    ): UserRepository {
        return UserRepositoryImpl(userApi, userDao)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        cartApi: CartApi,
        cartDao: CartDao
    ): CartRepository {
        return CartRepositoryImpl(cartApi, cartDao)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        orderApi: OrderApi,
        orderDao: OrderDao
    ): OrderRepository {
        return OrderRepositoryImpl(orderApi, orderDao)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(
        paymentApi: PaymentApi
    ): PaymentRepository {
        return PaymentRepositoryImpl(paymentApi)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(
        reviewApi: ReviewApi
    ): ReviewRepository {
        return ReviewRepositoryImpl(reviewApi)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        favoriteDao: FavoriteDao
    ): FavoriteRepository {
        return FavoriteRepositoryImpl(favoriteDao)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        productApi: ProductApi,
        searchHistoryDao: SearchHistoryDao
    ): SearchRepository {
        return SearchRepositoryImpl(productApi, searchHistoryDao)
    }

    @Provides
    @Singleton
    fun provideAddressRepository(
        addressDao: AddressDao
    ): AddressRepository {
        return AddressRepositoryImpl(addressDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        userApi: UserApi,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepositoryImpl(userApi, userDao)
    }
}
