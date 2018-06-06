package me.jdowns.matter.views.fragments.profilebottomsheetdialog

import android.support.annotation.WorkerThread
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.room.user.UserDao
import javax.inject.Inject

@WorkerThread
class ProfileBottomSheetDialogPresenter(private val view: ProfileBottomSheetDialogFragment) {
    @Inject
    internal lateinit var userDao: UserDao

    init {
        Matter.dependencyGraph.inject(this)
    }

    fun onLogOutClicked() {
        userDao.setLoggedOut(Matter.accountHelper.reddit.me().username)
        Matter.accountHelper.switchToUserless()
        launch(UI) {
            view.dismissView()
        }
    }
}