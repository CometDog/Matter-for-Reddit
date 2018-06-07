package me.jdowns.matter.views.activities.main

import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.R
import me.jdowns.matter.views.fragments.SubmissionFragment

@UiThread
class MainActivity : AppCompatActivity() {
    private val presenter = MainPresenter(this)
    private val navigator = MainNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        setUpProfileButton()
        setUpInitialView()
    }

    private fun setUpInitialView() {
        launch {
            presenter.onTryLogIn()
            launch(UI) {
                navigator.showInitialSubmissionView()
            }
        }
    }

    private fun setUpProfileButton() = findViewById<ImageButton>(R.id.action_bar_profile).setOnClickListener {
        navigator.showProfileBottomSheet()
    }

    fun setUpUserSpecificView(realUser: Boolean) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation_bar).apply {
            visibility = if (realUser) View.VISIBLE else View.GONE
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_front_page -> navigator.showInitialSubmissionView()
                    R.id.navigation_subreddits -> navigator.showSubredditsView()
                }
                true
            }
        }
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

    override fun recreate() {
        supportFragmentManager.findFragmentByTag(SubmissionFragment.FRAGMENT_TAG)?.run 
            supportFragmentManager.beginTransaction().remove(this).commitNow()
        }
        super.recreate()
    }
}
