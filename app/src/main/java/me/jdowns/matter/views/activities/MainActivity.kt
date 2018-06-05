package me.jdowns.matter.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.fragments.ProfileBottomSheetDialogFragment
import me.jdowns.matter.views.fragments.SubmissionFragment
import me.jdowns.matter.views.fragments.SubredditFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        findViewById<ImageButton>(R.id.action_bar_profile).setOnClickListener {
            ProfileBottomSheetDialogFragment().show(
                supportFragmentManager,
                ProfileBottomSheetDialogFragment.FRAGMENT_TAG
            )
        }

        launch(UI) {
            setUpViewForUser()
        }
    }

    @UiThread
    private suspend fun setUpViewForUser() {
        launch {
            setUpUser()
        }.join()
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)!!.apply {
            visibility = if (Matter.isRealUser()) View.VISIBLE else View.GONE
            setOnNavigationItemSelectedListener {
                addFragment(it)
                true
            }
        }
        findViewById<ViewGroup>(R.id.bottom_navigation_bar).visibility =
                if (Matter.isRealUser()) View.VISIBLE else View.GONE
        addFragment()
    }

    @WorkerThread
    private fun setUpUser() {
        try {
            Matter.accountHelper.switchToUser(Matter.provideDatabase().userDao().getLoggedIn()?.username!!)
        } catch (e: Exception) {
            Matter.accountHelper.switchToUserless()
        }
    }

    private fun addFragment(menuItem: MenuItem? = null) {
        with(bottomNavigationView) {
            if (visibility == View.GONE) {
                addInitialSubmissionView()
            } else {
                if (menuItem?.itemId != selectedItemId) {
                    when (menuItem?.itemId ?: selectedItemId) {
                        R.id.navigation_front_page -> addInitialSubmissionView()
                        R.id.navigation_subreddits -> addSubredditsView()
                    }
                }
            }
        }
    }

    private fun addInitialSubmissionView() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_main,
                SubmissionFragment.newInstance(if (Matter.isRealUser()) null else getString(R.string.all)),
                SubmissionFragment.FRAGMENT_TAG
            )
            .commit()
    }

    private fun addSubredditsView() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_main,
                SubredditFragment(),
                SubredditFragment.FRAGMENT_TAG
            )
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            recreate()
        } else if (resultCode != RESULT_CANCELED) {
            /** TODO: Handle other results from OAuthActivity */
            Toast.makeText(applicationContext, R.string.authentication_issue, Toast.LENGTH_LONG).show()
        }
    }
}
