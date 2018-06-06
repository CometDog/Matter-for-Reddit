package me.jdowns.matter.views.activities.main

import android.support.annotation.UiThread
import kotlinx.coroutines.experimental.async
import me.jdowns.matter.Matter
import me.jdowns.matter.room.user.UserDao
import javax.inject.Inject

class MainPresenter(private val view: MainActivity) {
    @Inject
    internal lateinit var userDao: UserDao

    init {
        Matter.dependencyGraph.inject(this)
    }

    @UiThread
    suspend fun onTryLogIn() {
        try {
            Matter.accountHelper.switchToUser(async {
                userDao.getLoggedIn()
            }.await()?.username!!)
            view.setUpUserSpecificView(true)
        } catch (e: Exception) {
            Matter.accountHelper.switchToUserless()
            view.setUpUserSpecificView(false)
        }
    }
}