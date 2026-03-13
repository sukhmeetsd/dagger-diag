package com.example.ecommerce.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<Any>
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<Any>
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    suspend fun getAll(): List<Any>
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders")
    suspend fun getAll(): List<Any>
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    suspend fun getAll(): List<Any>
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history")
    suspend fun getAll(): List<Any>
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses")
    suspend fun getAll(): List<Any>
}
