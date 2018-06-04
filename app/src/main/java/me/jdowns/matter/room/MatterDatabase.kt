package me.jdowns.matter.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import me.jdowns.matter.room.user.UserDao
import me.jdowns.matter.room.user.UserEntity

@Database(entities = [(UserEntity::class)], version = 1)
abstract class MatterDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

interface MatterDatabaseProvider {
    fun provideDatabase(): MatterDatabase
}