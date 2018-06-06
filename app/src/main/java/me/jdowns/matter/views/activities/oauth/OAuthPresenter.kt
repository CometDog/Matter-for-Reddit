package me.jdowns.matter.views.activities.oauth

import android.graphics.Bitmap
import android.support.annotation.WorkerThread
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.room.user.UserDao
import me.jdowns.matter.room.user.UserEntity
import javax.inject.Inject

@WorkerThread
class OAuthPresenter(private val view: OAuthActivity) {
    @Inject
    internal lateinit var userDao: UserDao

    init {
        Matter.dependencyGraph.inject(this)
    }

    suspend fun onCreated() {
        val oAuthHelper = async {
            Matter.accountHelper.switchToNewUser()
        }.await()
        val webViewClient = object : WebViewClient() {
            override fun onPageStarted(webView: WebView, url: String, favicon: Bitmap?) {
                if (oAuthHelper.isFinalRedirectUrl(url)) {
                    view.startLoading()
                    webView.stopLoading()
                    launch {
                        oAuthHelper.onUserChallenge(url).authManager.tokenStore
                        userDao.insert(UserEntity(Matter.accountHelper.reddit.me().username, 1))
                        launch(UI) {
                            view.stopLoading()
                        }
                    }
                } else {
                    super.onPageStarted(webView, url, favicon)
                }
            }
        }
        launch(UI) {
            view.setUpWebView(oAuthHelper, webViewClient)
        }
    }
}