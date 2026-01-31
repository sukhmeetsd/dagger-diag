package com.example.ecommerce.data.remote

import retrofit2.http.GET

// API Service interfaces
interface ProductApi {
    @GET("products")
    suspend fun getProducts(): List<Any>
}

interface UserApi {
    @GET("user")
    suspend fun getUser(): Any
}

interface CartApi {
    @GET("cart")
    suspend fun getCart(): Any
}

interface OrderApi {
    @GET("orders")
    suspend fun getOrders(): List<Any>
}

interface PaymentApi {
    @GET("payment")
    suspend fun processPayment(): Any
}

interface ReviewApi {
    @GET("reviews")
    suspend fun getReviews(): List<Any>
}
