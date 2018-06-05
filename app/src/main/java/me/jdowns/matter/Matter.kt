package me.jdowns.matter

import android.app.Application
import android.arch.persistence.room.Room
import android.util.Log
import me.jdowns.matter.room.MatterDatabase
import me.jdowns.matter.room.MatterDatabaseProvider
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

    init {
        application = this
    }

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

    companion object : MatterDatabaseProvider {
        var application: Matter? = null
        lateinit var accountHelper: AccountHelper
        lateinit var tokenStore: SharedPreferencesTokenStore
        private var database: MatterDatabase? = null

        fun isRealUser(): Boolean =
            accountHelper.isAuthenticated() && accountHelper.reddit.authMethod != AuthMethod.USERLESS_APP

        @Synchronized
        override fun provideDatabase(): MatterDatabase {
            return database ?: kotlin.run {
                database = Room.databaseBuilder(
                    application!!.applicationContext,
                    MatterDatabase::class.java,
                    "MatterDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                return database!!
            }
        }
    }
}