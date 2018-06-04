package me.jdowns.matter.views.fragments

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.activities.OAuthActivity

class ProfileBottomSheetDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.profile_username)!!.let {
            if (Matter.isRealUser()) {
                it.text = Matter.accountHelper.reddit.me().username
            } else {
                it.text = getString(R.string.log_in)
                it.setOnClickListener({
                    startActivityForResult(
                        Intent(activity, OAuthActivity::class.java),
                        OAuthActivity.OAUTH_REQUEST_CODE
                    )
                    dismiss()
                })
            }
        }
    }

    companion object {
        const val FRAGMENT_TAG = "profileBottomSheetDialogFragment"
    }
}