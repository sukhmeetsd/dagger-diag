package com.example.app.data

import retrofit2.http.GET

/**
 * API service interface for network calls
 */
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("posts")
    suspend fun getPosts(): List<Post>
}

data class User(val id: Int, val name: String, val email: String)
data class Post(val id: Int, val title: String, val body: String)
