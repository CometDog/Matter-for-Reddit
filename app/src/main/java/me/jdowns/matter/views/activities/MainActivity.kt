package me.jdowns.matter.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {
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
                addInitialSubmissionView()
            }
        }
    }

    private fun setUpUserless() {
        Matter.accountHelper.switchToUserless()
        findViewById<ViewGroup>(R.id.bottom_navigation_bar).visibility = View.GONE
    }

    private fun addInitialSubmissionView() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_main,
                SubmissionFragment.newInstance(if (Matter.isRealUser()) null else "all"),
                SubmissionFragment.FRAGMENT_TAG
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
