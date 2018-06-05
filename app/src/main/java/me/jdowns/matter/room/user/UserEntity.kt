package me.jdowns.matter.room.user

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class UserEntity(@PrimaryKey @ColumnInfo(name = "username") var username: String, @ColumnInfo(name = "is_logged_in") var isLoggedIn: Int)