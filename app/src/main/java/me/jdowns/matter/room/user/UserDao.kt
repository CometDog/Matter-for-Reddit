package me.jdowns.matter.room.user

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface UserDao {
    @Query("UPDATE userentity SET is_logged_in = 0 WHERE username = :username")
    fun setLoggedOut(username: String)

    @Query("UPDATE userentity SET is_logged_in = 1 WHERE username = :username")
    fun setLoggedIn(username: String)

    @Query("SELECT username FROM userentity")
    fun getAllUsernames(): List<String>?

    @Query("SELECT * FROM userentity WHERE is_logged_in = 1 LIMIT 1")
    fun getLoggedIn(): UserEntity?

    @Insert
    fun insert(userEntity: UserEntity)
}