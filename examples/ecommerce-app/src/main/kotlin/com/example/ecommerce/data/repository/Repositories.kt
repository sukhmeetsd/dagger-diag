package com.example.ecommerce.data.repository

import com.example.ecommerce.data.local.*
import com.example.ecommerce.data.remote.*

// Repository interfaces
interface ProductRepository
interface UserRepository
interface CartRepository
interface OrderRepository
interface PaymentRepository
interface ReviewRepository
interface FavoriteRepository
interface SearchRepository
interface AddressRepository
interface AuthRepository

// Repository implementations
class ProductRepositoryImpl(
    private val productApi: ProductApi,
    private val productDao: ProductDao
) : ProductRepository

class UserRepositoryImpl(
    private val userApi: UserApi,
    private val userDao: UserDao
) : UserRepository

class CartRepositoryImpl(
    private val cartApi: CartApi,
    private val cartDao: CartDao
) : CartRepository

class OrderRepositoryImpl(
    private val orderApi: OrderApi,
    private val orderDao: OrderDao
) : OrderRepository

class PaymentRepositoryImpl(
    private val paymentApi: PaymentApi
) : PaymentRepository

class ReviewRepositoryImpl(
    private val reviewApi: ReviewApi
) : ReviewRepository

class FavoriteRepositoryImpl(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository

class SearchRepositoryImpl(
    private val productApi: ProductApi,
    private val searchHistoryDao: SearchHistoryDao
) : SearchRepository

class AddressRepositoryImpl(
    private val addressDao: AddressDao
) : AddressRepository

class AuthRepositoryImpl(
    private val userApi: UserApi,
    private val userDao: UserDao
) : AuthRepository
