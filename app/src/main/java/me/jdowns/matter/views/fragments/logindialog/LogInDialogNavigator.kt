package me.jdowns.matter.views.fragments.logindialog

import android.content.Intent
import me.jdowns.matter.views.activities.oauth.OAuthActivity

class LogInDialogNavigator(private val view: LogInDialogFragment) {
    fun showOAuthView() = view.startActivityForResult(
        Intent(view.activity, OAuthActivity::class.java),
        OAuthActivity.OAUTH_REQUEST_CODE
    )
}