package me.jdowns.matter

import android.app.Application
import android.util.Log
import net.dean.jraw.RedditClient
import net.dean.jraw.android.AndroidHelper
import net.dean.jraw.android.ManifestAppInfoProvider
import net.dean.jraw.android.SharedPreferencesTokenStore
import net.dean.jraw.android.SimpleAndroidLogAdapter
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.oauth.AccountHelper
import net.dean.jraw.oauth.AuthMethod
import java.util.*

/** TODO: Investigate address fault on start up */
// Fatal signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 0x18 in tid 31986 (onPool-worker-2), pid 31887 (e.jdowns.matter)
class Matter : Application() {
    override fun onCreate() {
        super.onCreate()

        tokenStore = SharedPreferencesTokenStore(applicationContext).apply {
            load()
            autoPersist = true
        }

        accountHelper =
                AndroidHelper.accountHelper(ManifestAppInfoProvider(applicationContext), UUID.randomUUID(), tokenStore)
                    .apply {
                        onSwitch { redditClient: RedditClient ->
                            redditClient.logger = SimpleHttpLogger(
                                SimpleHttpLogger.DEFAULT_LINE_LENGTH,
                                SimpleAndroidLogAdapter(Log.INFO)
                            )
                        }
                    }
    }

    companion object {
        lateinit var accountHelper: AccountHelper
        lateinit var tokenStore: SharedPreferencesTokenStore

        fun isRealUser(): Boolean =
            accountHelper.isAuthenticated() && accountHelper.reddit.authMethod != AuthMethod.USERLESS_APP
    }
}