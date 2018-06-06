package me.jdowns.matter.views.fragments.logindialog

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
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.R

@UiThread
class LogInDialogFragment : android.support.v4.app.DialogFragment() {
    private val presenter = LogInDialogPresenter(this)
    private val navigator = LogInDialogNavigator(this)

    private val usernames = mutableListOf<String>()
    private lateinit var usernameSpinner: Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_fragment_log_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUserNameSpinner()
        setUpLogInButton()
        setUpNewUserButton()

    }

    private fun setUpUserNameSpinner() {
        usernameSpinner = view!!.findViewById<Spinner>(R.id.log_in_spinner).apply {
            adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, usernames)
        }
        launch {
            presenter.onUsernameSpinnerAdapterSet()
        }
    }

    fun updateAdapter(usernames: List<String>) {
        this.usernames.addAll(usernames)
        (usernameSpinner.adapter as ArrayAdapter<*>).notifyDataSetChanged()
    }

    fun handleNoSavedUsers() {
        dismissFamily()
        navigator.showOAuthView()
    }

    private fun setUpLogInButton() = view!!.findViewById<TextView>(R.id.log_in_button).setOnClickListener({
        launch {
            presenter.onLogInButtonClicked(view!!.findViewById<Spinner>(R.id.log_in_spinner).selectedItem.toString())
        }
    })

    private fun setUpNewUserButton() = view!!.findViewById<Button>(R.id.new_user_button).setOnClickListener({
        navigator.showOAuthView()
    })

    fun logInSucceeded() {
        dismissFamily()
        activity?.recreate()
    }

    private fun dismissFamily() {
        dismiss()
        (parentFragment as DialogFragment).dismiss()
    }

    companion object {
        const val FRAGMENT_TAG = "logInDialogFragmentTag"
    }
}