package com.example.app.data

/**
 * Repository interface for data operations
 */
interface Repository {
    suspend fun fetchAndStoreUsers()
    suspend fun getUsers(): List<User>
    suspend fun getPosts(): List<Post>
}

/**
 * Implementation of Repository that coordinates between API and database
 */
class RepositoryImpl(
    private val apiService: ApiService,
    private val userDao: UserDao
) : Repository {

    override suspend fun fetchAndStoreUsers() {
        val users = apiService.getUsers()
        users.forEach { user ->
            userDao.insertUser(user)
        }
    }

    override suspend fun getUsers(): List<User> {
        return userDao.getAllUsers()
    }

    override suspend fun getPosts(): List<Post> {
        return apiService.getPosts()
    }
}
