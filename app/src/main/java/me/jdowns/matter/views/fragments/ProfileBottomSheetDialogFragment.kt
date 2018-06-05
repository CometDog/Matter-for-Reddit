package me.jdowns.matter.views.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.R

class ProfileBottomSheetDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val usernameTextView = view.findViewById<TextView>(R.id.profile_username)
        val logoutLayoutView = view.findViewById<ViewGroup>(R.id.profile_logout_layout)

        super.onViewCreated(view, savedInstanceState)
        if (Matter.isRealUser()) {
            usernameTextView.text = Matter.accountHelper.reddit.me().username
            logoutLayoutView.apply {
                setOnClickListener({
                    showLogoutDialog()
                })
            }
        } else {
            usernameTextView.apply {
                text = getString(R.string.log_in)
                setOnClickListener({
                    LogInDialogFragment().show(childFragmentManager, LogInDialogFragment.FRAGMENT_TAG)
                })
            }
            logoutLayoutView.visibility = View.GONE
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(context)
            .setMessage(R.string.confirm_log_out)
            .setCancelable(true)
            .setPositiveButton(android.R.string.yes, { _, _ ->
                launch(UI) {
                    logOut()
                }
            })
            .setNegativeButton(android.R.string.no, { dialog, _ ->
                dialog.cancel()
            }).create().show()
    }

    @UiThread
    private suspend fun logOut() {
        launch {
            Matter.provideDatabase().userDao().setLoggedOut(Matter.accountHelper.reddit.me().username)
        }.join()
        dismiss()
        activity!!.recreate()
    }

    companion object {
        const val FRAGMENT_TAG = "profileBottomSheetDialogFragment"
    }
}