package me.jdowns.matter.views.fragments.profilebottomsheetdialog

import android.app.AlertDialog
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.R

@UiThread
class ProfileBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val presenter = ProfileBottomSheetDialogPresenter(this)
    private val navigator = ProfileBottomSheetDialogNavigator(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (Matter.isRealUser()) {
            setUpUsernameView()
            setUpLogOutView()
        } else {
            setUpLogInView()
            hideLogOutView()
        }
    }

    private fun setUpUsernameView() {
        view!!.findViewById<TextView>(R.id.profile_username).text = Matter.accountHelper.reddit.me().username
    }

    private fun setUpLogInView() = with(view!!.findViewById<TextView>(R.id.profile_username)) {
        text = getString(R.string.log_in)
        navigator.showLogInDialogView()
    }

    private fun setUpLogOutView() = view!!.findViewById<ViewGroup>(R.id.profile_logout_layout).setOnClickListener({
        showLogoutDialog()
    })

    private fun hideLogOutView() {
        view!!.findViewById<ViewGroup>(R.id.profile_logout_layout).visibility = View.GONE
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(context)
            .setMessage(R.string.confirm_log_out)
            .setCancelable(true)
            .setPositiveButton(android.R.string.yes, { _, _ ->
                launch {
                    presenter.onLogOutClicked()
                }
            })
            .setNegativeButton(android.R.string.no, { dialog, _ ->
                dialog.cancel()
            }).create().show()
    }

    fun dismissView() {
        dismiss()
        activity!!.recreate()
    }

    companion object {
        const val FRAGMENT_TAG = "profileBottomSheetDialogFragment"
    }
}