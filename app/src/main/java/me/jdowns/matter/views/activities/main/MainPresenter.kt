package me.jdowns.matter.views.activities.main

import android.support.annotation.WorkerThread
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.room.user.UserDao
import javax.inject.Inject

@WorkerThread
class MainPresenter(private val view: MainActivity) {
    @Inject
    internal lateinit var userDao: UserDao

    init {
        Matter.dependencyGraph.inject(this)
    }

    suspend fun onTryLogIn() {
        try {
            Matter.accountHelper.switchToUser(async {
                userDao.getLoggedIn()
            }.await()?.username!!)
        } catch (e: Exception) {
            Matter.accountHelper.switchToUserless()
        } finally {
            launch(UI) {
                view.setUpUserSpecificView(Matter.isRealUser())
            }
        }
    }
}