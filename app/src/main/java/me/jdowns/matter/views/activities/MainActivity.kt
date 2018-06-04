package me.jdowns.matter.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
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
        setUpUser()
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar).apply {
            setOnNavigationItemSelectedListener {
                addFragment(it)
                true
            }
        }
    }

    private fun setUpUser() {
        async {
            val loggedUser = Matter.provideDatabase().userDao().getLoggedIn()?.username
            async(UI) {
                if (loggedUser.isNullOrBlank()) {
                    setUpUserless()
                } else {
                    try {
                        Matter.accountHelper.switchToUser(loggedUser!!)
                        findViewById<ViewGroup>(R.id.bottom_navigation_bar).visibility = View.VISIBLE
                    } catch (e: Exception) {
                        setUpUserless()
                    }
                }
                addFragment()
            }
        }
    }

    private fun setUpUserless() {
        try {
            Matter.accountHelper.switchToUserless()
        } catch (e: Throwable) {
            Matter.accountHelper.switchToNewUser()
        } finally {
            findViewById<ViewGroup>(R.id.bottom_navigation_bar).visibility = View.GONE
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
            setUpUser()
        } else {
            Toast.makeText(applicationContext, R.string.authentication_issue, Toast.LENGTH_LONG).show()
        }
    }
}
