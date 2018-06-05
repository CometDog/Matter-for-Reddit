package me.jdowns.matter.views.activities

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.BuildConfig
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.room.user.UserEntity
import net.dean.jraw.oauth.StatefulAuthHelper

class OAuthActivity : AppCompatActivity() {
    private val scopes =
        "edit, history, identity, livemanage, modconfig, modcontributors, modmail, modothers, modposts, modself, mysubreddits, privatemessages, read, report, save, submit, subscribe, vote"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)

        displayOAuthPage()
    }

    private fun displayOAuthPage() {
        val oAuthHelper = Matter.accountHelper.switchToNewUser()

        findViewById<WebView>(R.id.oauth_webview)!!.also {
            it.webViewClient = OAuthWebViewClient(oAuthHelper, this)
            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }.loadUrl(oAuthHelper.getAuthorizationUrl(true, true, scopes))
    }

    private class OAuthWebViewClient(val authHelper: StatefulAuthHelper, val activity: AppCompatActivity) :
        WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            if (authHelper.isFinalRedirectUrl(url)) {
                activity.findViewById<ViewGroup>(R.id.oauth_progress_bar_layout).visibility = View.VISIBLE
                view.stopLoading()
                launch {
                    authHelper.onUserChallenge(url).authManager.tokenStore
                    Matter.provideDatabase().userDao().insert(UserEntity(Matter.accountHelper.reddit.me().username, 1))
                    launch(UI) {
                        activity.apply {
                            setResult(Activity.RESULT_OK)
                        }.finish()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    companion object {
        const val OAUTH_REQUEST_CODE = 0
    }
}