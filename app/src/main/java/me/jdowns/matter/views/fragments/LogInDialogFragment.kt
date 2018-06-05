package me.jdowns.matter.views.fragments

import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.room.user.UserDao
import me.jdowns.matter.views.activities.OAuthActivity
import javax.inject.Inject

class LogInDialogFragment : android.support.v4.app.DialogFragment() {
    @Inject
    lateinit var userDao: UserDao
    private val usernames = mutableListOf<String>()
    private lateinit var usernameSpinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Matter.dependencyGraph.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_fragment_log_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameSpinner = view.findViewById<Spinner>(R.id.log_in_spinner).apply {
            adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, usernames)
        }

        view.findViewById<TextView>(R.id.log_in_button).setOnClickListener({
            launch(UI) {
                logInUser(view.findViewById<Spinner>(R.id.log_in_spinner).selectedItem.toString())
            }
        })

        view.findViewById<Button>(R.id.new_user_button).setOnClickListener({
            startOAuthActivity()
        })

        launch(UI) {
            setUpView()
        }
    }

    @UiThread
    private suspend fun setUpView() {
        async {
            userDao.getAllUsernames()
        }.await()?.let {
            if (it.isNotEmpty()) {
                usernames.addAll(it)
                (usernameSpinner.adapter as ArrayAdapter<*>).notifyDataSetChanged()
            } else {
                startOAuthActivity()
            }
        } ?: startOAuthActivity()
    }

    @UiThread
    private suspend fun logInUser(username: String) {
        launch {
            userDao.setLoggedIn(username)
        }.join()
        dismissFamily()
        activity?.recreate()
    }

    private fun startOAuthActivity() {
        dismissFamily()
        startActivityForResult(
            Intent(activity, OAuthActivity::class.java),
            OAuthActivity.OAUTH_REQUEST_CODE
        )
    }

    private fun dismissFamily() {
        dismiss()
        (parentFragment as DialogFragment).dismiss()
    }

    companion object {
        const val FRAGMENT_TAG = "logInDialogFragmentTag"
    }
}