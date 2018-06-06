package me.jdowns.matter

import android.app.Application
import android.util.Log
import me.jdowns.matter.dagger.ApplicationComponent
import me.jdowns.matter.dagger.DaggerApplicationComponent
import me.jdowns.matter.dagger.modules.RoomModule
import net.dean.jraw.RedditClient
import net.dean.jraw.android.AndroidHelper
import net.dean.jraw.android.ManifestAppInfoProvider
import net.dean.jraw.android.SharedPreferencesTokenStore
import net.dean.jraw.android.SimpleAndroidLogAdapter
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.oauth.AccountHelper
import net.dean.jraw.oauth.AuthMethod
import java.util.*

class Matter : Application() {
    override fun onCreate() {
        super.onCreate()
        dependencyGraph = DaggerApplicationComponent.builder().roomModule(RoomModule(this)).build()

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
        @JvmStatic
        lateinit var dependencyGraph: ApplicationComponent
        lateinit var accountHelper: AccountHelper
        lateinit var tokenStore: SharedPreferencesTokenStore

        fun isRealUser(): Boolean =
            accountHelper.isAuthenticated() && accountHelper.reddit.authMethod != AuthMethod.USERLESS_APP
    }
}