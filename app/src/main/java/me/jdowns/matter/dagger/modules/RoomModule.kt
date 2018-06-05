package me.jdowns.matter.dagger.modules

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import me.jdowns.matter.room.MatterDatabase
import me.jdowns.matter.room.user.UserDao
import javax.inject.Singleton

@Module
class RoomModule(application: Application) {
    private val database: MatterDatabase =
        Room.databaseBuilder(application, MatterDatabase::class.java, "MatterDatabase").build()

    @Provides
    @Singleton
    fun provideDatabase(): MatterDatabase = database

    @Provides
    @Singleton
    fun provideUserDao(database: MatterDatabase): UserDao = database.userDao()
}