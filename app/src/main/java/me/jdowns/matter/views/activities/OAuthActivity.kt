package me.jdowns.matter.views.activities

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
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

    private class OAuthWebViewClient(val authHelper: StatefulAuthHelper, val activity: AppCompatActivity?) :
        WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            if (authHelper.isFinalRedirectUrl(url)) {
                view.stopLoading()
                async {
                    authHelper.onUserChallenge(url).authManager.tokenStore
                    Matter.provideDatabase().userDao().insert(UserEntity(Matter.accountHelper.reddit.me().username, 1))
                    async(UI) {
                        activity?.setResult(Activity.RESULT_OK)
                        activity?.finish()
                    }
                }
            }
        }
    }

    companion object {
        const val OAUTH_REQUEST_CODE = 0
    }
}