package me.jdowns.matter.views.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.jdowns.matter.R
import me.jdowns.matter.views.fragments.SubmissionFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_main, SubmissionFragment.newInstance("all"), SubmissionFragment.FRAGMENT_TAG)
            .commit()
    }
}
