package me.jdowns.matter.views.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.jdowns.matter.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
    }
}
