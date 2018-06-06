package me.jdowns.matter.views.fragments.logindialog

import android.support.annotation.WorkerThread
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.room.user.UserDao
import javax.inject.Inject

@WorkerThread
class LogInDialogPresenter(private val view: LogInDialogFragment) {
    @Inject
    internal lateinit var userDao: UserDao

    init {
        Matter.dependencyGraph.inject(this)
    }

    fun onLogInButtonClicked(username: String) {
        userDao.setLoggedIn(username)
        launch(UI) {
            view.logInSucceeded()
        }
    }

    fun onUsernameSpinnerAdapterSet() = userDao.getAllUsernames()?.let {
        launch(UI) {
            if (it.isNotEmpty()) {
                view.updateAdapter(it)
            } else {
                view.handleNoSavedUsers()
            }
        }
    } ?: launch(UI) { view.handleNoSavedUsers() }
}