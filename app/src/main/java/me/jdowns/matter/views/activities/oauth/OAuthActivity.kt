package me.jdowns.matter.views.activities.oauth

import android.app.Activity
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import me.jdowns.matter.BuildConfig
import me.jdowns.matter.R
import net.dean.jraw.oauth.StatefulAuthHelper

@UiThread
class OAuthActivity : AppCompatActivity() {
    private val presenter = OAuthPresenter(this)
    private val scopes =
        "edit, history, identity, livemanage, modconfig, modcontributors, modmail, modothers, modposts, modself, mysubreddits, privatemessages, read, report, save, submit, subscribe, vote"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_oauth)
        presenter.onCreated()
    }

    fun startLoading() {
        findViewById<ProgressBar>(R.id.oauth_progress_bar_layout).visibility = View.VISIBLE
    }

    fun stopLoading() {
        findViewById<ProgressBar>(R.id.oauth_progress_bar_layout).visibility = View.GONE
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun setUpWebView(oAuthHelper: StatefulAuthHelper, webViewClient: WebViewClient) {
        findViewById<WebView>(R.id.oauth_webview)!!.apply {
            this.webViewClient = webViewClient
            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }.loadUrl(oAuthHelper.getAuthorizationUrl(true, true, scopes))
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    companion object {
        const val OAUTH_REQUEST_CODE = 0
    }
}