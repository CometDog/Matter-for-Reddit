package me.jdowns.matter.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.activities.OAuthActivity
import me.jdowns.matter.views.widgets.BaseBottomSheetDialog
import me.jdowns.matter.views.widgets.BaseBottomSheetDialogFragment

class ProfileBottomSheetDialogFragment : BaseBottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): BaseBottomSheetDialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setContentView(R.layout.dialog_fragment_profile)
        }
    }

    override fun onDialogViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view.findViewById<TextView>(R.id.profile_username)) {
            if (Matter.isRealUser()) {
                text = Matter.accountHelper.reddit.me().username
            } else {
                text = getString(R.string.log_in)
                setOnClickListener({
                    startActivityForResult(
                        Intent(activity, OAuthActivity::class.java),
                        OAuthActivity.OAUTH_REQUEST_CODE
                    )
                    this@ProfileBottomSheetDialogFragment.dismiss()
                })
            }
        }
    }

    companion object {
        const val FRAGMENT_TAG = "profileBottomSheetDialogFragment"
    }
}