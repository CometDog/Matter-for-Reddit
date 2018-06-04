package me.jdowns.matter.room.user

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM userentity WHERE is_logged_in = 1 LIMIT 1")
    fun getLoggedIn(): UserEntity?

    @Insert
    fun insert(userEntity: UserEntity)
}