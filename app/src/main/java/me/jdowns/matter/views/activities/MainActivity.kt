package me.jdowns.matter.views.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.experimental.async
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.fragments.AllFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_main, AllFragment(), AllFragment.FRAGMENT_TAG)
            .commit()
        async {
            Matter.accountHelper.switchToUserless().subreddit("all").posts()
        }
    }
}
