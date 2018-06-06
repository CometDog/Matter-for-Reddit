package me.jdowns.matter.views.activities.main

import android.support.annotation.UiThread
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.fragments.ProfileBottomSheetDialogFragment
import me.jdowns.matter.views.fragments.SubmissionFragment
import me.jdowns.matter.views.fragments.SubredditFragment

@UiThread
class MainNavigator(private val view: MainActivity) {
    fun showInitialSubmissionView() {
        view.supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_main,
                SubmissionFragment.newInstance(if (Matter.isRealUser()) null else view.getString(R.string.all)),
                SubmissionFragment.FRAGMENT_TAG
            )
            .commit()
    }

    fun showSubredditsView() {
        view.supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_main,
                SubredditFragment(),
                SubredditFragment.FRAGMENT_TAG
            )
            .commit()
    }

    fun showProfileBottomSheet() {
        ProfileBottomSheetDialogFragment().show(
            view.supportFragmentManager,
            ProfileBottomSheetDialogFragment.FRAGMENT_TAG
        )
    }
}