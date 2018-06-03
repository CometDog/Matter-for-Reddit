package me.jdowns.matter.views.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.fragments.ProfileBottomSheetDialogFragment
import me.jdowns.matter.views.fragments.SubmissionFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        if (!Matter.isRealUser()) {
            findViewById<ViewGroup>(R.id.bottom_navigation_bar).visibility = View.GONE
        }
        findViewById<ImageButton>(R.id.action_bar_profile).setOnClickListener {
            ProfileBottomSheetDialogFragment().show(
                supportFragmentManager,
                ProfileBottomSheetDialogFragment.FRAGMENT_TAG
            )
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_main, SubmissionFragment.newInstance("all"), SubmissionFragment.FRAGMENT_TAG)
            .commit()
    }
}
