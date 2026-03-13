package com.example.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Data access object for User entities
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert
    suspend fun insertUser(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}
