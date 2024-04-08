package com.uninsubria.myvideoteca

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<User>
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    fun getUser(username: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User)
    @Delete
    suspend fun delete(user: User)
}